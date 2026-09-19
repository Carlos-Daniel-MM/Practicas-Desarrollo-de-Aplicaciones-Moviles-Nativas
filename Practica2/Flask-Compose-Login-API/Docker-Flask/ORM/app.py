from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask import request, jsonify
from itsdangerous import URLSafeTimedSerializer
import os
from functools import wraps
from flask import request, jsonify

def requiere_sesion(f):
    @wraps(f)
    def decorada(*args, **kwargs):
        # Android nos enviará un token en los encabezados HTTP
        token = request.headers.get('Authorization')
        
        if not token:
            # Código 401 obligatorio por la rúbrica
            return jsonify({"error": "Sesión inválida o ausente"}), 401
            
        return f(*args, **kwargs)
    return decorada

app = Flask(__name__)

# 1. Configuración de la Base de Datos (SQLite)
# El archivo se guardará en la carpeta del contenedor como 'site.db'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SECRET_KEY'] = 'mi_llave_secreta_para_el_reporte' # En un proyecto real, esto va en variables de entorno
generador_tokens = URLSafeTimedSerializer(app.config['SECRET_KEY'])


db = SQLAlchemy(app)
bcrypt = Bcrypt(app)

# 2. Modelo de Usuario (La tabla en la BD)
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(20), unique=True, nullable=False)
    password = db.Column(db.String(60), nullable=False) # Aquí guardaremos el hash

    def __repr__(self):
        return f"User('{self.username}')"

class Tarea(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    titulo = db.Column(db.String(100), nullable=False)
    descripcion = db.Column(db.String(250), nullable=True)
    # Guardamos el ID del usuario dueño de la tarea por seguridad
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)

    def to_dict(self):
        return {
            "id": self.id,
            "titulo": self.titulo,
            "descripcion": self.descripcion,
            "user_id": self.user_id
        }

# 3. Rutas

@app.route('/')
def hello():
    return jsonify({"message": "API Funcionando"})

# Endpoint de REGISTRO
@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')

    # Verificar si el usuario ya existe
    if User.query.filter_by(username=username).first():
        return jsonify({"message": "El usuario ya existe"}), 400

    # Encriptar contraseña
    hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
    
    # Crear y guardar nuevo usuario
    new_user = User(username=username, password=hashed_password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "Usuario creado exitosamente"}), 201

# Endpoint de LOGIN
@app.route('/login', methods=['POST'])
def login():
    datos = request.get_json()
    username = datos.get('username')
    password = datos.get('password')
    usuario = User.query.filter_by(username=username).first()
    #Verificamos que exista y que su contraseña hasheada sea correcta
    if usuario and bcrypt.check_password_hash(usuario.password, password):
        # ¡AQUÍ CREAMOS EL TOKEN! Guardamos su ID adentro del token de forma segura
        token = generador_tokens.dumps({'user_id': usuario.id})
        #Se lo enviamos a la aplicación de Android
        return jsonify({
            "mensaje": "Inicio de sesión exitoso",
            "token": token
        }), 200
    # Si puso mal su usuario o contraseña, lo rebotamos con un error 401
    return jsonify({"error": "Credenciales inválidas"}), 401

@app.route('/tareas', methods=['POST'])
@requiere_sesion
def crear_tarea():
    # Aquí deberíamos validar la sesión (lo haremos en el siguiente paso para cumplir la rúbrica)
    datos = request.get_json()
    
    if not datos or not datos.get('titulo'):
        return jsonify({"error": "El título es obligatorio"}), 400
        
    nueva_tarea = Tarea(
        titulo=datos['titulo'],
        descripcion=datos.get('descripcion', ''),
        user_id=1 # Por ahora le ponemos 1, luego lo amarramos al usuario logueado
    )
    
    db.session.add(nueva_tarea)
    db.session.commit()
    
    return jsonify({"mensaje": "Tarea creada exitosamente", "tarea": nueva_tarea.to_dict()}), 201

# --- LEER (GET) ---
@app.route('/tareas', methods=['GET'])
@requiere_sesion
def obtener_tareas():
    tareas = Tarea.query.all()
    return jsonify([tarea.to_dict() for tarea in tareas]), 200

# --- ACTUALIZAR (PUT) ---
@app.route('/tareas/<int:id>', methods=['PUT'])
@requiere_sesion
def actualizar_tarea(id):
    tarea = Tarea.query.get(id)
    if not tarea:
        return jsonify({"error": "Tarea no encontrada"}), 404
        
    datos = request.get_json()
    if 'titulo' in datos:
        tarea.titulo = datos['titulo']
    if 'descripcion' in datos:
        tarea.descripcion = datos['descripcion']
        
    db.session.commit()
    return jsonify({"mensaje": "Tarea actualizada", "tarea": tarea.to_dict()}), 200

# --- BORRAR (DELETE) ---
@app.route('/tareas/<int:id>', methods=['DELETE'])
@requiere_sesion
def borrar_tarea(id):
    tarea = Tarea.query.get(id)
    if not tarea:
        return jsonify({"error": "Tarea no encontrada"}), 404
        
    db.session.delete(tarea)
    db.session.commit()
    return jsonify({"mensaje": "Tarea eliminada exitosamente"}), 200

if __name__ == '__main__':
    # Esto crea las tablas automáticamente si no existen al iniciar
    with app.app_context():
        db.create_all()
    
    app.run(host='0.0.0.0', port=5000, debug=True)
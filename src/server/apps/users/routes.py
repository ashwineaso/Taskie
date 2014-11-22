from bottle import Bottle
from apps.users.views import *

users_app = Bottle()

users_app.route('/register', 'POST', register)

__author__ = ["ashwineaso"]
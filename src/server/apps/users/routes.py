from bottle import Bottle
from apps.users.views import *

users_app = Bottle()

users_app.route('/register', 'POST', register)
users_app.route('/authorize', 'POST', authorize_user)
users_app.route('/issuetoken', 'POST', issueToken)
users_app.route('/refreshtoken', 'POST', refreshTokens)
users_app.route('/checkaccesstoken', 'POST', checkAccessToken)

__author__ = ["ashwineaso"]
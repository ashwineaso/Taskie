from bottle import Bottle
from apps.users.views import *

users_app = Bottle()

users_app.route('/register', 'POST', register)
users_app.route('/verifyUser', 'POST', verifyUser)
users_app.route('/updateUser', 'POST', updateUser)
users_app.route('/authorize', 'POST', authorize_user)
# users_app.route('/issuetoken', 'POST', issueToken)
users_app.route('/refreshtokens', 'POST', refreshTokens)
users_app.route('/checkaccesstoken', 'POST', checkAccessToken)
users_app.route('/addProfilePic', 'POST', addProfilePic)

__author__ = ["ashwineaso"]
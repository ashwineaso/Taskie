from bottle import Bottle
from apps.users.views import *

users_app = Bottle()

users_app.route('/register', 'POST', register)
users_app.route('/verifyEmail/<email>/<key>', 'GET', verifyEmail)
users_app.route('/verifyUser', 'POST', verifyUser)
users_app.route('/setServerPushId', 'POST', setServerPushId)
users_app.route('/updateUser', 'POST', updateUser)
users_app.route('/authorize', 'POST', authorize_user)
# users_app.route('/issuetoken', 'POST', issueToken)
users_app.route('/refreshtokens', 'POST', refreshTokens)
users_app.route('/checkaccesstoken', 'POST', checkAccessToken)
users_app.route('/modifyProfilePic', 'POST', modifyProfilePic)
users_app.route('/syncUserInfo', 'POST', syncUserInfo)
users_app.route('/passwordReset', 'POST', passwordReset)
users_app.route('/updatePassword/<email>/<key>', 'GET', updatePassword)
users_app.route('/doUpdatePassword', 'POST', doUpdatePassword)

users_app.route('*\/<filename:re:.*\.css>', 'GET', stylesheets)
users_app.route('*\/<filename:re:.*\.js>', 'GET', javascripts)
users_app.route('*\/<filename:re:.*\.(jpg|png|gif|ico)>', 'GET', images)
users_app.route('*\/<filename:re:.*\.(eot|ttf|woff|svg)>', 'GET', fonts)

__author__ = ["ashwineaso"]
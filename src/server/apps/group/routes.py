from bottle import Bottle
from apps.group.views import *

group_app = Bottle()

group_app.route('/createGroup', 'POST', createGroup)
group_app.route('/addGroupMembers', 'POST', addGroupMembers)
group_app.route('/remGroupMembers', 'POST', remGroupMembers)
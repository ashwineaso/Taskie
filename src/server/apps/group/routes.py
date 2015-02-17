from bottle import Bottle
from apps.group.views import *

group_app = Bottle()

group_app.route('/addNewTask', 'POST', addNewTask)
group_app.route('/editTask', 'POST', editTask)
group_app.route('/addCollaborators', 'POST', addCollaborators)
group_app.route('/remCollaborators', 'POST', remCollaborators)
group_app.route('/modifyTaskStatus', 'POST', modifyTaskStatus)
group_app.route('/modifyCollStatus', 'POST', modifyCollStatus)
group_app.route('/syncTask', 'POST', syncTask)
group_app.route('/createGroup', 'POST', createGroup)
group_app.route('/remGroupMembers', 'POST', remGroupMembers)
group_app.route('/remGroupMembers', 'POST', remGroupMembers)
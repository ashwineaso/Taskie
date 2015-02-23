__author__ = ["ashwineaso"]

from bottle import Bottle 
from apps.task.views import *

task_app = Bottle()

task_app.route('/addNewTask', 'POST', addNewTask)
task_app.route('/editTask', 'POST', editTask)
task_app.route('/addCollaborators', 'POST', addCollaborators)
task_app.route('/remCollaborators', 'POST', remCollaborators)
task_app.route('/modifyTaskStatus', 'POST', modifyTaskStatus)
task_app.route('/modifyCollStatus', 'POST', modifyCollStatus)
task_app.route('/syncTask', 'POST', syncTask)
task_app.route('/syncAllTasks', 'POST', syncAllTasks)
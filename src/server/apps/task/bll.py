__author__ = ["ashwineaso"]
from . import dal

def addNewTask(taskObj):
	"""
	Adds a new task to the task list

	:type taskObj : object
	:para. taskObj : An object with the following attributes
			uuid,
			owner,
			collaborators,
			priority,
			name,
			description,
			dueDateTime,
			status
	:return an object of the task calss.
	"""

	#Add a task to the servers task list
	task = dal.addNewTask(taskObj)
	return task
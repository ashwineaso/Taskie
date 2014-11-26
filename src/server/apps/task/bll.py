__author__ = ["ashwineaso"]
from . import dal
from settings.exceptions import TaskWithIDNotFound

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
	:return an object of the task class.
	"""

	#Add a task to the servers task list
	task = dal.addNewTask(taskObj)
	return task

def editTask(taskObj):
	"""
	Edits and existing taskObj

	:type taskObj : object
	:para taskObj : An object with the following attributes
			_id,
			name,
			description,
			dueDateTime,
			priority
	:return : an object of task class
	"""

	#Search task using id
	try:
		taskObj.task = dal.getTaskByID(taskObj)
		#Update database with task information
		task = dal.editTask(taskObj)
		return task
	except TaskWithIDNotFound as e:
		raise e
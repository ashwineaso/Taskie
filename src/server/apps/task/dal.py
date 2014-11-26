__author__ = ["ashwineaso"]
from apps.task.models import *
from apps.users import bll as userbll
from settings.altEngine import Collection
import datetime


def addNewTask(taskObj):
	"""
	Creates a task

	"""

	#Assigning initial status to each task
	taskObj.status = Status(
					status = 0,
					dateTime = datetime.datetime.now()
					)

	#Define an emptly list to include all the user objects
	my_objects = []
	userObj = Collection()
	
	#Creating the list of collaborators
	for val in taskObj.collaborators:
		userObj.email = val
		my_objects.append(Collaborator(user = userbll.getUserByEmail(userObj),
										status = taskObj.status))
	
	#Create a task with the necessary data.
	task = Task(
		owner = taskObj.owner,
		collaborators = my_objects,
		priority = taskObj.priority,
		name = taskObj.name,
		description = taskObj.description,
		dueDateTime = taskObj.dueDateTime,
		status = taskObj.status
		).save()
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
	: return : an object of task class

	"""
	task = Task.objects.get(id = taskObj.id)
	Task.objects(id = task.id).update(
										set__name = taskObj.name,
										set__description = taskObj.description,
										set__priority = taskObj.priority,
										set__dueDateTime = taskObj.dueDateTime)
	task.reload()
	return task



def getTaskByID(taskObj):
	"""
	Get the task using its id and return Task object

	:type taskObj: object
	:param taskObj: An instance with the following attributes
			id,
			name,
			description,
			priority,
			dueDateTime
	:return An instance of the Task class

	"""
	try:
		task = Task.objects.get(id = taskObj.id)
		return task
	except DoesNotExist as e:
		raise TaskwithIDNotFound
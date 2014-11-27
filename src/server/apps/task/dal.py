__author__ = ["ashwineaso"]
from apps.task.models import *
from apps.users import bll as userbll
from settings.altEngine import Collection
from bson.objectid import ObjectId
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


def addCollaborators(taskObj):
	"""
	Add collaborators to an existing task

	:type taskObj: object
	:param taskObj: An instance with the following attributes
			collaborators
	:return An instance of the Task class

	"""

	my_objects = []
	userObj = Collection()

	#Assigning initial status to each collaborator
	taskObj.status = Status(
					status = 0,
					dateTime = datetime.datetime.now()
					)

	#Get the user - collaborator id and add to list
	for val in taskObj.collaborators:
		userObj.email = val
		my_objects.append(Collaborator(user = userbll.getUserByEmail(userObj),
										status = taskObj.status))

	task = Task.objects.get(id = taskObj.id)
	Task.objects(id = task.id).update( push_all__collaborators = my_objects)
	
	task.reload()
	return task


def remCollaborators(taskObj):
	"""
	Remove collaborators from an existing task

	:type taskObj: object
	:param taskObj: An instance with the following attributes
			collaborators
	:return An instance of the Task class

	"""

	coll = Collaborator()
	task = Task.objects(id = taskObj.id).get()
	for coll in  task.collaborators:
		if (coll.user.email == taskObj.collaborators):
			Task.objects(id = task.id).update( pull__collaborators = coll)
	task.reload()
	return task


def modifyTaskStatus(taskObj):
	"""
	Modfiy the status of the existing taskObj

	type taskObj: object
	:param taskObj: An instance with the following attributes
			id
			status
	:return An instance of the Task class

	"""

	statusObj = Status()
	statusObj = Status(status = taskObj.status,
						dateTime = taskObj.dateTime)
	task  = Task.objects(id = taskObj.id).get()
	Task.objects(id = taskObj.id).update(set__status = statusObj)
	task.reload()
	return task
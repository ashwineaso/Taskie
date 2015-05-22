__author__ = ["ashwineaso"]
from apps.task.models import *
from apps.users import bll as userbll
from settings.altEngine import Collection
from bson.objectid import ObjectId
import time
from settings.exceptions import *


def addNewTask(taskObj):
	"""
	Adds a new task to the task list

	:type taskObj : object
	:para. taskObj : An object with the following attributes
			owner,
			collaborators,
			priority,
			name,
			description,
			dueDateTime,
			status

	:return an object of the task class.

	"""

	#Assigning initial status to each task
	taskObj.task_status = Status(
					status = 1,
					dateTime = time.time()
					)
	taskObj.coll_status = Status(
					status = 0,
					dateTime = time.time()
					)

	#Define an emptly list to include all the user objects
	my_objects = []
	userObj = Collection()

	#for finding the user id of the owner
	userObj.id = taskObj.owner
	taskObj.owner = userbll.getUserById(userObj)
	
	#Checking whether the collaborators is a user of the app
	#If not - Create a new account for the user
	for userObj.email in taskObj.collaborators:
		try:
			User.objects.get(email = userObj.email)
		except Exception:
			userbll.createAndInvite(userObj) #contains _id = senders id and email = recievers email

	#Creating the list of collaborators
	for val in taskObj.collaborators:
		userObj.email = val
		my_objects.append(Collaborator(user = userbll.getUserByEmail(userObj),
										status = taskObj.coll_status))
	
	#Create a task with the necessary data.
	task = Task(
				owner = taskObj.owner,
				collaborators = my_objects,
				priority = taskObj.priority,
				name = taskObj.name,
				description = taskObj.description,
				dueDateTime = taskObj.dueDateTime,
				status = taskObj.task_status
				)
	task.save()
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
	task = getTaskById(taskObj)
	Task.objects(id = task.id).update(
										set__name = taskObj.name,
										set__description = taskObj.description,
										set__priority = taskObj.priority,
										set__dueDateTime = taskObj.dueDateTime)
	task.reload()
	return task


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
					dateTime = time.time()
					)

	task = getTaskById(taskObj)
	userObj.owner = task.owner
	#Check if new collaborators exist
	for userObj.email in taskObj.collaborators:
		try:
			#try to find the user by email from the databsae
			person = User.objects.get(email = userObj.email)
		except Exception as e:
			#If user is not found, create a new minimal users and send an invite
			person = userbll.createAndInvite(userObj)
		#Get the returned user from both cases and add create collaborators from them
		my_objects.append(Collaborator(user = person,
										status = taskObj.status))

	#Update the task with the new collaborator
	Task.objects(id = task.id).update( push_all__collaborators = my_objects)
	task.save()
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
	userObj = Collection()
	task = getTaskById(taskObj)
	for userObj.email in  taskObj.collaborators:
		user = userbll.getUserByEmail(userObj)
		Task.objects(id = taskObj.id).update_one( pull__collaborators__user = user)
	task.save()
	task.reload()
	return task


def modifyTaskStatus(taskObj):
	"""
	Modfiy the status of the existing taskObj

	type taskObj:	object
	:param taskObj:	An instance with the following attributes
					id
					status
	:return An instance of the Task class

	"""

	statusObj = Status()
	statusObj = Status(status = taskObj.status,
						dateTime = time.time())
	task  = getTaskById(taskObj)
	Task.objects(id = taskObj.id).update(set__status = statusObj)
	task.reload()
	return task


def modifyCollStatus(taskObj):
	"""
	Modify the status of the collaborator

	:type taskObj : object
	:param taskObj :An instance with the following attributes
					id - id of the task
					email - email of the collaborator
					collstatus - new status of the collaborator
	:return An instance of the Collaborator class
	"""

	task = getTaskById(taskObj)
	userObj = userbll.getUserByEmail(taskObj)
	collaborator = [x for x in task.collaborators if x.user == userObj]
	collaborator[0].status.status = taskObj.collstatus
	collaborator[0].status.dateTime = time.time()
	task.save()
	return task


def syncTask(taskObj):
	"""
	Sync / retrieve as task whose id is provided
	"""
	task = getTaskById(taskObj)
	return task


def deleteTask(taskObj):
	"""Delete a task and update all collaborators about it"""
	task = getTaskById(taskObj)
	statusObj = Status(status = -1,
						dateTime = time.time())
	try:
		Task.objects(id = task.id).update(set__status = statusObj)
		task.reload()
		return True
	except Exception as e:
		return False


def syncAllTasks(taskObj):
	"""
	Sync all tasks of user of which is he owner and collaborator
	"""
	user = userbll.getUserById(taskObj)
	owner_list = list(Task.objects(owner = user))
	collaborator_list = list(Task.objects(collaborators__user = user))
	task_list = owner_list + collaborator_list
	return task_list


def getTaskById(taskObj):
	"""
	Retrieve task using task id
	::type taskObj : object
	::parm taskObj : An instance of Collection with the following attributes
					 id
	::return task : Instance of Task class

	"""

	try:
		task = Task.objects.get(id = taskObj.id)
		return task
	except Exception as e:
		raise TaskWithIDNotFound
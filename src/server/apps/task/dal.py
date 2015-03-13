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
	taskObj.status = Status(
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
	userObj.user = task.owner
	#Check if new collaborators exist
	for userObj.email in taskObj.collaborators:
		flag = False
		for person in User.objects:
			#If user exists in Server, flag is marked true and continues
			if person.email == userObj.email:
				flag = True
		#If flag is false, create a new user and send invite
		if flag is False:
			userbll.createAndInvite(userObj)

	#Get the user - collaborator id and add to list
	for userObj.email in taskObj.collaborators:
		my_objects.append(Collaborator(user = userbll.getUserByEmail(userObj),
										status = taskObj.status))


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
					collemail - email of the collaborator
					collstatus - new status of the collaborator
					statusDateTime - dateTime of status update
	:return An instance of the Collaborator class
	"""

	task = getTaskById(taskObj)
	userObj = userbll.getUserByEmail(taskObj)
	for collaborator in task.collaborators:
		if collaborator.user == userObj:
			collaborator.status.status = taskObj.collstatus
			collaborator.status.dateTime = time.time()
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
	statusObj = Status(status = 2,
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
	owner_list = list(Task.objects(Q(owner = user) & Q(status__status = 0)))
	collaborator_list = list(Task.objects(Q(collaborators__user = user) & Q(status__status = 0)))
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
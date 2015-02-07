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
			isgroup
			group

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
			userbll.createAndInvite(userObj)

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
				status = taskObj.status,
				isgroup = taskObj.isgroup
				).save()
	if taskObj.isgroup is True:
		taskObj.group = Group.objects.get(id = taskObj.group)
		Task.objects(id = task.id).update(set__group = taskObj.group)
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

	task = getTaskById(taskObj)
	Task.objects(id = task.id).update( push__collaborators = my_objects)
	task.save()
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

	type taskObj:	object
	:param taskObj:	An instance with the following attributes
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

	task = Task.objects.get(id = taskObj.id).select_related(1)
	userObj = userbll.getUserByEmail(taskObj)
	for collaborator in task.collaborators:
		if collaborator.user == userObj:
			collaborator.status.status = taskObj.collstatus
			collaborator.status.dateTime = taskObj.statusDateTime
	task.save()
	return task


def createGroup(groupObj):
	"""

	Create a new group
	:type groupObj : object
	:param groupObj : An instance with the following attributes
						ownerId - userId of the creator/ member
						title - name of the groupObj
	:return group : An instance of the Group class
	"""

	userObj = Collection()

	#for finding the user id of the owner
	userObj.email = groupObj.owner
	groupObj.owner = userbll.getUserByEmail(userObj)
	group = Group(
					owner = groupObj.owner,
					title = groupObj.title).save()
	return group


def syncTask(taskObj):
	"""
	Sync / retrieve as task whose id is provided
	"""
	task = getTaskById(taskObj)
	return task


def getTaskById(taskObj):
	"""
	Retrieve task using task id
	::type taskObj : object
	::parm userObj : An instance of Collection with the following attributes
					 id
	::return task : Instance of Task class

	"""

	try:
		task = Task.objects.get(id = taskObj.id).select_related(1)
		return task
	except Exception:
		raise TaskWithIDNotFound
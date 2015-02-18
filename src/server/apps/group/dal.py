__author__ = ["ashwineaso"]
from models import *
from apps.task.models import * 
from apps.users import bll as userbll
from mongoengine import DoesNotExist
from settings.exceptions import *
import time
import os.path
from settings.altEngine import Collection

taskObj = Collection()
groupObj = Collection()

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
	members = []

	#for finding the user id of the owner
	userObj.id = groupObj.owner
	groupObj.user = userbll.getUserById(userObj)
	taskgroup = TaskGroup(
					owner = groupObj.user,
					title = groupObj.title
					)
	taskgroup.save()
	members.append(groupObj.user)
	TaskGroup.objects(id = taskgroup.id).update(push_all__members = members)
	taskgroup.reload()
	return taskgroup



def addGroupMembers(groupObj):
	"""
	Add members to a groupObj

	:type groupObj : object
	:param groupObj : An instance of Collection with the following attributes
						id - id of the TaskGroup
						member - list of members to be added to the group
	:return : An instance of the Group class
	"""

	member_list = []
	userObj = Collection

	taskgroup = getGroupById(groupObj)
	for userObj.id in groupObj.members:
		userObj.user = userbll.getUserById(userObj)
		member_list.append(userObj.user)

	TaskGroup.objects(id = taskgroup.id).update(push_all__members = member_list)
	taskgroup.save()
	taskgroup.reload()
	return taskgroup



def remGroupMembers(groupObj):
	"""
	Remove memberd from a group

	:type groupObj : object
	:param groupObj : An instance of Collection with the following attributes
						id - id of the TaskGroup
						member - list of members to be added to the group
	:return : An instance of the Group class
	"""

	userObj = Collection()

	taskgroup = getGroupById(groupObj)
	for userObj.id in groupObj.members:
		user = userbll.getUserById(userObj)
		TaskGroup.objects(id = taskgroup.id).update_one(pull__members = user)
	taskgroup.save()
	taskgroup.reload()
	return taskgroup


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
	
	#Find if the group exists. If Yes, get TaskGroup object
	groupObj.id = taskObj.group_id
	taskgroup = getGroupById(groupObj)

	#Checking whether the collaborators is a user of the app
	#If yes check whether he is a member of the group
	for userObj.email in taskObj.collaborators:
		user = userbll.getUserByEmail(userObj)
		if not [x for x in taskgroup.members if x.id == user.id]:
			raise UserNotMember

	#Creating the list of collaborators
	for userObj.email in taskObj.collaborators:
		my_objects.append(Collaborator(user = userbll.getUserByEmail(userObj),
										status = taskObj.status))
	
	#Create a task with the necessary data.
	task = GroupTask(
				owner = taskObj.owner,
				collaborators = my_objects,
				priority = taskObj.priority,
				name = taskObj.name,
				description = taskObj.description,
				dueDateTime = taskObj.dueDateTime,
				status = taskObj.status,
				collaborator_count = taskObj.collaborator_count
				).save()

	#Add the GroupTask object to the TaskGroup's task_list list
	try:
		TaskGroup.objects(id = taskgroup.id).update(push__task_list = task.id)
	except:
		raise GroupWithIDNotFound
	taskgroup.save()
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
										set__dueDateTime = taskObj.dueDateTime,
										set__collaborator_count = taskObj.collaborator_count)
	task.reload()
	return task


def addCollaborators(taskObj):
	"""
	Add collaborators to an existing task

	:type taskObj: object
	:param An instance with the following attributes
			collaborators
	:return An instance of the Task class

	"""
	my_objects = []
	userObj = Collection()
	groupObj = Collection()

	#Obtain the group using id
	groupObj.id = taskObj.group_id
	taskgroup = getGroupById(groupObj)

	#Obtain the task using task is
	task = getTaskById(taskObj)

	#Create a Status object to assign to each collaborator
	taskObj.status = Status(
					status = 0,
					dateTime = time.time()
					)

	#Checking existence of collaborators
	for userObj.email in taskObj.collaborators:
		user = userbll.getUserByEmail(userObj)
		if not [x for x in taskgroup.members if x.id == user.id]:
			raise UserNotMember
		my_objects.append(Collaborator(user = user,
										status = taskObj.status))

	GroupTask.objects(id = task.id).update(push_all__collaborators = my_objects)
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
	pass



def getGroupById(groupObj):
	"""
	Retrieve group using id
	::type groupObj : object
	::param groupObj :Instance of Collection with the following attributes
						id
	::return group : Instance if TaskGroup class
	"""

	try:
		group = TaskGroup.objects.get(id = groupObj.id)
		return group 
	except Exception as e:
		raise GroupWithIDNotFound



def getTaskById(taskObj):
	"""
	Retrieve Task.GroupTask using id
	::type taskObj : objecy
	::param : id
	::return task : Instance of Task.GroupTask class
	"""

	try:
		task = GroupTask.objects.get(id = taskObj.id)
		return task
	except Exception as e:
		raise e
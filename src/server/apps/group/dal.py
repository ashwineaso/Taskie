__author__ = ["ashwineaso"]
from models import *
from apps.users import bll as userbll
from mongoengine import DoesNotExist
from settings.exceptions import *
import time
import os.path
from settings.altEngine import Collection

taskObj = Collection()
userObj = Collection()
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

	#for finding the user id of the owner
	userObj.id = groupObj.owner
	groupObj.user = userbll.getUserById(userObj)
	group = TaskGroup(
					owner = groupObj.user,
					title = groupObj.title
					)
	group.save()
	return group



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

	group = getGroupById(groupObj)
	for userObj.id in groupObj.members:
		userObj.user = userbll.getUserById(userObj)
		member_list.append(userObj.user.id)
	TaskGroup.objects(id = group.id).update(push_all__members = member_list)
	group.save()
	group.reload()
	return group



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

	group = getGroupById(groupObj)
	for userObj.id in groupObj.members:
		user = userbll.getUserById(userObj)
		TaskGroup.objects(id = group.id).update_one(pull__members = user)
	group.save()
	group.reload()
	return group


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
	
	#Checking whether the collaborators is a user of the app
	#If not - Create a new account for the user
	for userObj.email in taskObj.collaborators:
		try:
			User.objects.get(email = userObj.email)
		except Exception:
			userbll.createAndInvite(userObj) #contains _id = senders id and email = recievers email

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
				collaborator_count = taskObj.Collaborator_count
				)

	#Finding and assigning the group
	groupObj.id = taskObj.group_id
	group = getGroupById(groupObj)
	#Add the GroupTask object to the TaskGroup's task_list list
	TaskGroup.objects(id = group.id).update(push_all__task_list = task)
	group.save()
	return task



def getGroupById(groupObj):
	"""
	Retrieve group using id
	::type groupObj : object
	::param groupObj :Instance of Collection with the following attributes
						id
	::return group : Instance if TaskGroup class
	"""

	try:
		group = TaskGroup.objects.get(id = groupObj.id).select_related(1)
		return group 
	except Exception as e:
		raise GroupWithIDNotFound
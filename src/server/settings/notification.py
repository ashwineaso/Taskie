__author__ = ["ashwineaso"]

import time
from apps.users import bll as userbll
from settings.altEngine import Collection

class Notification():
	"""docstring for Notification"""

	NOTIFICATION_TYPE = {
		"New_Task" = "newTask"
		"Task_Update" = "taskUpdate"
		"Task_Status_Change" = "taskStatusChange"
		"Task_Deleted" = "taskDeletion"
		"Collaborator_Added" = "collAddition"
		"Collaborator_Deleted" = "collDeletion"
	}

	message = Collection()

	def __init__(self, arg):
		super(Notification, self).__init__()
		self.arg = arg

	def taskAdded(task):
		message["type"] = NOTIFICATION_TYPE["New_Task"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["dateTime"] = time.time()
		return message


	def taskDetailsChange(task):
		message["type"] = NOTIFICATION_TYPE["Task_Update"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["dateTime"] = time.time()
		return message


	def taskStatusChange(task):
		message["type"] = NOTIFICATION_TYPE["Task_Status_Change"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["status"] = task.status.status
		message["dateTime"] = time.time()
		return message		


	def taskDeletion(task):
		message["type"] = NOTIFICATION_TYPE["Task_Deleted"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["dateTime"] = time.time()
		return message		


	def collAddition(taskObj, task):
		message["type"] = NOTIFICATION_TYPE["Collaborator_Added"]
		message["ownerName"] = str(task.owner.name)
		message["unknown"] = 0
		message["dateTime"] = time.time()

		#Get the removed Collaborator using his mail
		for userObj.email in taskObj.Collaborators:
			user = userbll.getUserByEmail(userObj)
			if (user.name == null) :
				message["unknown"] +=1
			else:
				message["removedColl"].append(user.name)
		return message


	def collDeletion(taskObj, task):
		message["type"] = NOTIFICATION_TYPE["Collaborator_Deleted"]
		message["ownerName"] = str(task.owner.name)
		message["unknown"] = 0
		message["dateTime"] = time.time()

		#Get the removed Collaborator using his mail
		for userObj.email in taskObj.Collaborators:
			user = userbll.getUserByEmail(userObj)
			if (user.name == null) :
				message["unknown"] +=1
			else:
				message["removedColl"].append(user.name)
		return message
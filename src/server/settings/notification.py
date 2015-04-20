__author__ = ["ashwineaso"]

import time
from apps.users import bll as userbll
from settings.altEngine import Collection

class Notification():
	"""docstring for Notification"""

	NOTIFICATION_TYPE = {
		"New_Task" : "newTask",
		"Task_Update" : "taskUpdate",
		"Task_Status_Change" : "taskStatusChange",
		"Task_Deleted" : "taskDeletion",
		"Collaborator_Added" : "collAddition",
		"Collaborator_Deleted" : "collDeletion"
	}

	seconds_time = int(round(time.time()))

	def taskAdded(self, task):
		message = {}
		message["type"] = self.NOTIFICATION_TYPE["New_Task"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["dateTime"] = self.seconds_time
		return message


	def taskDetailsChange(self, task):
		message = {}
		message["type"] = self.NOTIFICATION_TYPE["Task_Update"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["dateTime"] = self.seconds_time
		return message


	def taskStatusChange(self, task):
		message = {}
		message["type"] = self.NOTIFICATION_TYPE["Task_Status_Change"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["status"] = task.status.status
		message["dateTime"] = self.seconds_time
		return message		


	def taskDeletion(self, task):
		message = {}
		message["type"] = self.NOTIFICATION_TYPE["Task_Deleted"]
		message["ownerName"] = str(task.owner.name)
		message["taskName"] = str(task.name)
		message["dateTime"] = self.seconds_time
		return message		


	def collAddition(self, taskObj, task):
		message = {}
		userObj = Collection()
		message["type"] = self.NOTIFICATION_TYPE["Collaborator_Added"]
		message["ownerName"] = str(task.owner.name)
		message["unknown"] = 0
		message["dateTime"] = self.seconds_time
		message["addedColl"] = ""

		#Get the removed Collaborator using his mail
		for userObj.email in taskObj.collaborators:
			user = userbll.getUserByEmail(userObj)
			if (user.name) :
				message["unknown"] +=1
			else:
				message["addedColl"] += user.name + ", "
		return message


	def collDeletion(self, taskObj, task):
		message = {}
		userObj = Collection()
		message["type"] = self.NOTIFICATION_TYPE["Collaborator_Deleted"]
		message["ownerName"] = str(task.owner.name)
		message["unknown"] = 0
		message["dateTime"] = self.seconds_time
		message["removedColl"] = []

		#Get the removed Collaborator using his mail
		for userObj.email in taskObj.collaborators:
			user = userbll.getUserByEmail(userObj)
			if (user.name) :
				message["unknown"] +=1
			else:
				message["removedColl"] += user.name + ", "
		return message
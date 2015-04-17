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

	message = {}

	def taskAdded(self,task):
		self.message["type"] = self.NOTIFICATION_TYPE["New_Task"]
		self.message["ownerName"] = str(task.owner.name)
		self.message["taskName"] = str(task.name)
		self.message["dateTime"] = time.time()
		return self.message


	def taskDetailsChange(task):
		self.message["type"] = self.NOTIFICATION_TYPE["Task_Update"]
		self.message["ownerName"] = str(task.owner.name)
		self.message["taskName"] = str(task.name)
		self.message["dateTime"] = time.time()
		return self.message


	def taskStatusChange(task):
		self.message["type"] = self.NOTIFICATION_TYPE["Task_Status_Change"]
		self.message["ownerName"] = str(task.owner.name)
		self.message["taskName"] = str(task.name)
		self.message["status"] = task.status.status
		self.message["dateTime"] = time.time()
		return self.message		


	def taskDeletion(task):
		self.message["type"] = self.NOTIFICATION_TYPE["Task_Deleted"]
		self.message["ownerName"] = str(task.owner.name)
		self.message["taskName"] = str(task.name)
		self.message["dateTime"] = time.time()
		return self.message		


	def collAddition(taskObj, task):
		self.message["type"] = self.NOTIFICATION_TYPE["Collaborator_Added"]
		self.message["ownerName"] = str(task.owner.name)
		self.message["unknown"] = 0
		self.message["dateTime"] = time.time()

		#Get the removed Collaborator using his mail
		for userObj.email in taskObj.Collaborators:
			user = userbll.getUserByEmail(userObj)
			if (user.name == null) :
				self.message["unknown"] +=1
			else:
				self.message["removedColl"].append(user.name)
		return self.message


	def collDeletion(taskObj, task):
		self.message["type"] = self.NOTIFICATION_TYPE["Collaborator_Deleted"]
		self.message["ownerName"] = str(task.owner.name)
		self.message["unknown"] = 0
		self.message["dateTime"] = time.time()

		#Get the removed Collaborator using his mail
		for userObj.email in taskObj.Collaborators:
			user = userbll.getUserByEmail(userObj)
			if (user.name == null) :
				self.message["unknown"] +=1
			else:
				self.message["removedColl"].append(user.name)
		return message
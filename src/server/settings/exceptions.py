class TaskWithIDNotFound(Exception):
	def __init__(self):
		super(TaskWithIDNotFound, self).__init__()
		self.message = "Cannot find the task with the ID"
		self .code = 2001


class  UserNotFound(Exception):
	def __init__(self):
		super(UserNotFound, self).__init__()
		self.message = "User not found in database"
		self.code = 1001
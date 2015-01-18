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


class UserAlreadyExists(Exception):
	"""docstring for ClassName"""
	def __init__(self):
		super(UserAlreadyExists, self).__init__()
		self.message = "User already exists in Database"
		self.code = 1002


class PasswordMismatch(Exception):
	"""docstring for ClassName"""
	def __init__(self):
		super(PasswordMismatch, self).__init__()
		self.message = "Username and Password does not match"
		slef.code = 1003
		
		
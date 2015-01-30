class TaskWithIDNotFound(Exception):
	"""docstring for TaskWithIDNotFound"""
	def __init__(self, arg):
		super(TaskWithIDNotFound, self).__init__()
		self.msg = "Task With ID Not Found"
		self.code = 2001

	def __str__(self):
		return self.msg


class UserNotFound(Exception):
	"""docstring for UserNotFound"""
	def __init__(self):
		super(UserNotFound, self).__init__()
		self.msg = "User Not Found"
		self.errcode = 1001 

	def __str__(self):
		return self.msg
		

class PasswordMismatch(Exception):
	"""docstring for PasswordMismatch"""
	def __init__(self):
		super(PasswordMismatch, self).__init__()
		self.msg = "Password Mismatch"
		self.code = 1002

	def __str__(self):
		return self.msg
		

class UserAlreadyExists(Exception):
	"""docstring for UserAlreadyExists"""
	def __init__(self):
		super(UserAlreadyExists,self).__init__()
		self.msg = "User already exists"
		self.code = 1003 

	def __str__(self):
		return self.msg


class UserPendingConfirmation(Exception):
	"""docstring for UserPendingConfirmation"""
	def __init__(self):
		super(UserPendingConfirmation, self).__init__()
		self.msg = "User Registered, Pending Confirmation"
		self.code = 1004
		
	def __str__(self):
		return self.msg
		

class TokenNotFound(Exception):
	"""docstring for TokenNotFound"""
	def __init__(self):
		super(TokenNotFound, self).__init__()
		self.msg = "Token Not Found"
		self.code = 3001

	def __str__(self):
		return self.msg
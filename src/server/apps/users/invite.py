__author__ = ["Ashwin Easo"]

import smtplib
from models import *
from . import dal
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText


def sendInvite(userObj):
	inviter = dal.getUserById(userObj)
	invited_by = inviter.email
	invite_to = userObj.email
	token = Token.objects.get(user = inviter)
	taskie_mail = "invites@taskie.me"
	HOST = "mail.taskie.me"
	PORT = "25"
	password = "wxqkqxr4$&$@"

	# Create message container - the correct MIME type is multipart/alternative.
	msg = MIMEMultipart('alternative')
	msg['Subject'] = inviter.name+" has invited you to taskie"
	msg['From'] = taskie_mail
	msg['To'] = invite_to

	# Create the body of the message (a plain-text and an HTML version).
	html = """\
	<html>
		<head></head>
		<body>
			<p>Hi!<br>
			How are you?<br>
			You have been invited to collaborate on Takise. Download Taskie from Google Play Store,<br>
			and simply click the link below.<br>
			www.play.google.com/com.altersense.taskie
			</p>
		</body>
	</html>
	"""

	html_msg = MIMEText(html, 'html')
	msg.attach(html_msg)

	#Send the message via local smtp server
	server = smtplib.SMTP()
	server.connect(HOST, PORT)
	server.starttls()
	server.login(taskie_mail,password)
	server.sendmail(taskie_mail,invite_to, msg.as_string())
	server.close()
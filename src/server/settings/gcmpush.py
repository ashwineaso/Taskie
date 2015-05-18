__author__ = ["ashwineaso"]

from settings.constants import GCMPost, TOKEN_GCM_REGISTRATION_IDS, UrlPostThread, UrlPost
from apps.users import dal as userdal
from apps.task import dal as taskdal
from apps.group import dal as groupdal
from settings.altEngine import Collection, SyncClass
from multiprocessing import Process

def pushSyncNotification(syncObj, taskObj = Collection()):
    """
    Initiates a server side push to all the collaborators
    of the task
    """

    #Create a GCMPost object for Android Push
    androidPush = GCMPost()
    #List to store GCM ids
    androidPayload = []
    userObj = Collection()

    #Create a pseudo switch case
    #Define a function to execute for each case
    def caseTask():
        """ Task is to be synced and message to be sent to owner and collaborators """
        task = taskdal.getTaskById(syncObj)
        for coll in task.collaborators:
            if not coll.user.serverPushId in androidPayload and (-1 < coll.status.status < 2):
                androidPayload.append(str(coll.user.serverPushId))
        androidPayload.append(str(task.owner.serverPushId))

    def caseGroup():
        """ Group is to be synced and message to be sent to all group members"""
        group = groupdal.getGroupById(syncObj)
        for member in group.members:
            if not member.serverPushId in androidPayload and (-1 < member.status.status < 2):
                androidPayload.append(str(member.serverPushId))
        androidPayload.append(str(group.owner.serverPushId))

    def caseBuzz():
        """Buzz all the collaborators of a task """
        task = taskdal.getTaskById(syncObj)
        for coll in task.collaborators:
            if not coll.user.serverPushId in androidPayload and (-1 < coll.status.status < 2):
                androidPayload.append(str(coll.user.serverPushId))

    def caseDelete():
        """Notfiy all the task users that the owner has deleted the task """
        for coll in taskObj.collaborators:
            if not coll.user.serverPushId in androidPayload and (-1 < coll.status.status < 2):
                androidPayload.append(str(coll.user.serverPushId))

    def caseCollRem():
        """Norify the collaborators that they have been removed"""
        task = taskdal.getTaskById(syncObj)
        for userObj.email in taskObj.collaborators:
            coll = userdal.getUserByEmail(userObj)
            if not coll.serverPushId in androidPayload:
                androidPayload.append(str(coll.serverPushId))

    #Define the lookup dictionary
    choice = {"Task":caseTask, "Group":caseGroup, "Buzz":caseBuzz, "Deleted":caseDelete, "CollRemoved":caseCollRem}

    choice[syncObj.datatype]() #to call appropriate case    
    

    if len(androidPayload) > 0:
        androidPush.payload[TOKEN_GCM_REGISTRATION_IDS] = androidPayload
        androidPush.payload["data"] = syncObj.to_dict()


        # Create UrlPoster Thread for GCM Push Start Thread
        # gcmPostThread = UrlPostThread(
        #                             threadID = 1,
        #                             name = 'gcmPostThread',
        #                             postObj = androidPush
        #                             )
        # gcmPostThread.start()

        postProcess = Process( target = UrlPost, args = (androidPush,))
        postProcess.start()
        postProcess.join()
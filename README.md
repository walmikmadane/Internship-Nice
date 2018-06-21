# Internship-Nice
A Generic AI Based Chatbot Application for customer support management..

# How to Chat with Telecom(Prebuild) Bot:
    step 1:Run webservice "webserviceExample" (on localhost with port 8080)
    step 2:Run webservice "subscriptionManagement"
    step 3:Run "Chatbotui/project/index.html" for chatting..
    
# How to Configure your own Bot: 
    step 1:create input json file as per format(use "EcommerceSupport.json" for help) and your rest api should be exposed...
    step 2:Run "aws/createbot.java" with json file input in "aws/UserConfigureInput.java"
    step 3: to chat with your bot refer to above guide "How to Chat with Telecom(Prebulid) Bot"..(make sure you change your bot name in index.html file).
    
# How to update Training dataset of your Bot under maintanance:
    step 1:Run "aws/UpdateBotWithNewTrainingdataset.java"..(make sure you change your botname)

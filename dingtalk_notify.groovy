//
// This script is used to post dingtalk message.
// Solution is based on https://gist.github.com/dongfg/a171bcbba33a871b2f1eb2d73d8fd10e
// 
import groovy.json.JsonOutput

if ( build_status != "SUCCESS" ) {
    currentBuild.result = 'FAILURE'
} else {
    currentBuild.result = 'SUCCESS'
}

send_msg(currentBuild.result)

def send_msg(result){
    stage('Send Message') {
        send_dingtalk_msg(result.equals("SUCCESS")?'Build Successful':'Build Failed')
    }
}

def send_dingtalk_msg(build_result) {
    def dingtalk_token = "xxxxxxxxxxx"
    def ding_task_url = "https://oapi.dingtalk.com/robot/send?access_token=${dingtalk_token}"
    def msg_data = """
            {
                "actionCard": {
                    "title": "Build Result",
                    "text": "### ${BUILD_TAG}\n**${build_result}**",
                    "hideAvatar": "0",
                    "btnOrientation": "0",
                    "singleTitle": "View Output",
                    "singleURL": "${BUILD_URL}/consoleText"
                },
                "msgtype": "actionCard",
                "hideAvatar": "1"
            }
        """

    request_post(ding_task_url, msg_data)
}

def request_post(endpoint, comment) {
    def url = new URL(endpoint)
    def url_connection = url.openConnection()

    url_connection.setRequestMethod("POST")
    url_connection.setDoOutput(true)
    url_connection.setRequestProperty("Content-Type", "application/json")

    url_connection.getOutputStream().write(comment.getBytes("UTF-8"));
    
    def rc_code = url_connection.getResponseCode();
    println(rc_code);
}

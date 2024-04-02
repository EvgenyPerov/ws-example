const socket = new SockJS('/our-websocket');
const stompClient = Stomp.over(socket);

const topic = '/commonTopic/messages';
const topicPrivate = '/user/privateTopic/';

const app = '/app/message';
const appPrivate = '/app/private-message/';

$(document).ready(function (){
    console.log("Index page is ready");
    connect();

    $("#send").click(function (){
        sendMessage();
    });

    $("#send-private").click(function (){
        sendPrivateMessage();
    });
});

function connect(){

    stompClient.connect({}, function (frame) {
        console.log("Connected:" + frame);
        let idCurrentUser =  frame.headers['user-name'];
        console.log("idCurrentUser=" + idCurrentUser);

        stompClient.subscribe(
            topic, function (message){
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe(
            topicPrivate + idCurrentUser + '/private-messages', function (message){
                showMessage(JSON.parse(message.body).content);
            });

        stompClient.subscribe(
            topicPrivate + idCurrentUser + '/errors', function(message) {
                console.log("Error " + message.body);
        });

    }, function(error) {
        console.log("STOMP error " + error);
    });
}

function sendMessage(){
    console.log("Sent common message");
    stompClient.send(app , {}, JSON.stringify({'name': $("#message").val()}));
}

function sendPrivateMessage(){
    let idToUser = $("#private-id").val();
    console.log("Sent private message to " + idToUser);
    //добавить подписку, если нужно, чтобы отправителю вернулся ответ на фронт
    stompClient.subscribe(
        topicPrivate + idToUser + '/private-messages', function (message){
            showMessage(JSON.parse(message.body).content);
        });

    stompClient.send(appPrivate + idToUser , {}, JSON.stringify({'name': $("#private-message").val()}));


}

function showMessage(message){
    $("#messages").append("<tr> <td>" + message + "</td> </tr>")
}
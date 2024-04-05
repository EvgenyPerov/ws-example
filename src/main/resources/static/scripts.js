const socket = new SockJS('/t-game/001/connect');
const stompClient = Stomp.over(socket);

const topic = '/commonTopic/'; // messages';
const topicPrivate = '/user/privateTopic/';

const app = '/app/message/';
const appPrivate = '/app/private-message/';

$(document).ready(function () {
    console.log("Index page is ready");

    // для примера передаем номер сессии в качестве параметра - 197
    var sessionId = 197;

    connect(sessionId);

    $("#send").click(function () {
        sendMessage(sessionId);
    });

    $("#send-private").click(function () {
        sendPrivateMessage(sessionId);
    });
});

function connect(sessionId) {

    stompClient.connect({}, function (frame) {
        console.log("Connecting status to session #" + sessionId + ": " + frame);

        // подписка на все сообщения
        stompClient.subscribe(
            topic + sessionId + "/messages", function (message) {
                showMessage1(sessionId, JSON.parse(message.body).content);
            });

        // получаем собственное имя (оно генерится на бэкэнде), для работы с личными сообщениями
        let idCurrentUser = frame.headers['user-name'];
        console.log("idCurrentUser=" + idCurrentUser);

        // подписка на личные сообщения для себя
        stompClient.subscribe(
            topicPrivate + sessionId + '/private-messages', function (message) {
                showMessage2(JSON.parse(message.body).content);
            });

        stompClient.subscribe(
            topicPrivate + '/errors', function (message) {
                console.log("Error " + message.body);
            });

        // эта подписка нужна чтобы отправителю вернулся ответ после отправки личных сообщений
        stompClient.subscribe(
            '/privateTopic/' + sessionId + '/private-messages', function (message) {
                showMessage2(JSON.parse(message.body).content);
            });

    }, function (error) {
        console.log("STOMP error " + error);
    });
}

function sendMessage(sessionId) {
    console.log("Sent common message");
    // отправка сообщения. Здесь объект содержит только поле 'name'
    stompClient.send(app + sessionId, {}, JSON.stringify({'name': $("#message").val()}));
}

function sendPrivateMessage(sessionId) {
    let idToUser = $("#private-id").val();  // в этом примере Id получателя берем с фронта, с input поля
    console.log("Sent private message to " + idToUser);
    // отправка личного сообщения. Здесь объект содержит только поле 'name'
    stompClient.send(appPrivate + sessionId +'/'+ idToUser, {}, JSON.stringify({'name': $("#private-message").val()}));
}

// простой вывод сообщений в таблицу
function showMessage2(message) {
    $("#messages").append("<tr> <td>" + message + "</td> </tr>")
}

function showMessage1(sessionId,  message) {
    $("#messages").append("<tr> <td>" + sessionId + " " + message + "</td> </tr>")
}
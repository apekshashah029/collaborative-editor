let stompClient = null;
let isRemoteUpdate = false;


const params = new URLSearchParams(window.location.search);
const docId = params.get("docId");

if (!docId) {
    alert("No document selected");
    window.location.href = "index.html";
}


function setCursorPosition(element, position) {
    const selection = window.getSelection();
    const range = document.createRange();

    let currentPos = 0;
    let nodeStack = [element];
    let node;

    while ((node = nodeStack.pop())) {
        if (node.nodeType === 3) {
            const nextPos = currentPos + node.length;
            if (position <= nextPos) {
                range.setStart(node, position - currentPos);
                range.collapse(true);
                selection.removeAllRanges();
                selection.addRange(range);
                return;
            }
            currentPos = nextPos;
        } else {
            let i = node.childNodes.length;
            while (i--) nodeStack.push(node.childNodes[i]);
        }
    }
}


function getCursorPosition(element) {
    const selection = window.getSelection();
    if (!selection.rangeCount) return 0;

    const range = selection.getRangeAt(0);
    const preRange = range.cloneRange();
    preRange.selectNodeContents(element);
    preRange.setEnd(range.endContainer, range.endOffset);

    return preRange.toString().length;
}



function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("Connected");

        stompClient.subscribe(`/topic/typing/${docId}`, (message) => {

            const editor = document.getElementById('editor');
            const cursorPos = getCursorPosition(editor);

            // Prevent infinite loop
            isRemoteUpdate = true;
            editor.innerText = message.body;
            isRemoteUpdate = false;

            setCursorPosition(editor, cursorPos);

        });

        stompClient.send(`/app/typing/${docId}`, {}, "");


    });
}

connect();

document.getElementById('editor').addEventListener('input', function () {
    if (isRemoteUpdate) return;

    const content = this.innerText;

    if (stompClient && stompClient.connected) {
        stompClient.send(`/app/typing/${docId}`, {}, content);
    }
});

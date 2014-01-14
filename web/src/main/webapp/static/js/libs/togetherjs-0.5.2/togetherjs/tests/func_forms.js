// =SECTION Setup

$("#fixture").append('<textarea id="textarea" style="width: 10em; height: 3em;"></textarea>');
$("#fixture").append('<br>');
$("#fixture").append('<div><label for="yes"><input type="radio" name="answer" id="yes"> Yes</label><label for="no"><input type="radio" name="answer" id="no"> No</label></div>');

Test.require("forms", "session", "ui", "windowing");
// => Loaded modules: ...

printChained(
  Test.resetSettings(),
  Test.startTogetherJS(),
  Test.closeWalkthrough());
// =>...

var $yes = $("#yes");
var $no = $("#no");
var $textarea = $("#textarea");

windowing.hide("#togetherjs-about");

// =SECTION Changes

Test.waitMessage("form-update");
$yes.prop("checked", true);
$yes.change();

/* =>
send: form-update
  clientId: "me",
  element: "#yes",
  value: true
*/

Test.waitMessage("form-update");
$no.prop("checked", true);
$no.change();

/* =>
send: form-update
  clientId: "me",
  element: "#no",
  value: true
*/

function selection() {
  var start = $textarea[0].selectionStart;
  var end = $textarea[0].selectionEnd;
  if (typeof start != "number") {
    if (typeof end == "number") {
      console.warn("Weird, end with no start", end);
    }
    return 'no selection';
  }
  print('selected', start, '-', end);
}

function select(start, end) {
  if (end === undefined) {
    end = start;
  }
  $textarea[0].selectionStart = start;
  $textarea[0].selectionEnd = end;
}

Test.waitMessage("form-update");
$textarea.val("hello");
$textarea.change();

/* =>
send: form-update
  clientId: "me",
  element: "#textarea",
  replace: {
    basis: 1,
    delta: {
      del: 0,
      start: 0,
      text: "hello"
    },
    id: "..."
  },
  "server-echo": true
*/

select(3, 4);
selection();

Test.waitMessage("form-update");
$textarea.val("hello there");
$textarea.change();

/* =>
send: form-focus...
selected 3 - 4
send: form-update
  clientId: "me",
  element: "#textarea",
  replace: {
    basis: 2,
    delta: {
      del: 0,
      start: 5,
      text: " there"
    },
    id: "..."
  },
  "server-echo": true
*/

// This doesn't seem to have a reliable result, but I don't know why...
// but I don't think it matters, since the change is only the result of
// $textarea.val()
selection();

Test.waitMessage("form-update");
$textarea.val("hi there");
$textarea.change();

/* =>
selected ? - ?
send: form-update
  clientId: "me",
  element: "#textarea",
  replace: {
    basis: 3,
    delta: {
      del: 4,
      start: 1,
      text: "i"
    },
    id: "..."
  },
  "server-echo": true
*/

select(3, 4);

Test.incoming({
  type: "hello",
  clientId: "faker",
  url: location.href.replace(/\#.*/, ""),
  urlHash: "",
  name: "Faker",
  avatar: "about:blank",
  color: "#ff0000",
  title: document.title,
  rtcSupported: false
});
Test.incoming({
  clientId: "faker",
  type: 'form-update',
  element: "#textarea",
  replace: {
    basis: 4,
    delta: {
      start: 1,
      del: 1,
      text: "ey"
    }
  }
});
wait(100);

/* =>

send: hello-back...
send: form-focus...
send: form-init
  clientId: "me",
  pageAge: ?,
  updates: [
    {
      basis: 5,
      element: "#textarea",
      value: "hey there"
    },
    {
      element: "#yes",
      value: false
    },
    {
      element: "#no",
      value: true
    }
  ]
*/

print($textarea.val());
selection();

/* =>
hey there
selected 4 - 5
*/

select(0, 5);
Test.incoming({
  clientId: "faker",
  type: 'form-update',
  element: "#textarea",
  replace: {
    basis: 5,
    delta: {
      start: 1, del: 2, text: "ELLO"
    }
  }
});
wait(100);

// =>

print($textarea.val());
selection();

/* =>
hELLO there
selected 0 - 7
*/

// form-init should be ignored in some cases...
print(Date.now() - TogetherJS.pageLoaded > 10);
Test.incoming({
  clientId: "faker",
  type: "form-init",
  pageAge: 10,
  updates: [
    {element: "#textarea",
     value: "foo"
    }
  ]
});
wait(100);

// => true

print($textarea.val());

// => hELLO there

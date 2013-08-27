//$(document).on("click", ".alert", function(e) {
//    bootbox.alert("Hello world!", function() {
//        console.log("Alert Callback");
//    });
//});


function confirmDelete($title, $message, $callback){
    bootbox.dialog({
        animate: true,
        message: $message,
        height:140,
        modal: true,
        resizable: false,
        title: $title,
        buttons: {
          delete_it: {
            label: "delete",
            className: "btn-danger",
            callback: $callback
          },
          cancel_it: {
            label: "cancel",
            className: "btn-success",
                callback: function() {}
          }
        }
    });
}

function test($message){

}
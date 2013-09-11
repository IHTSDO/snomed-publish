// APPLICATION
// -----------

App = Ember.Application.create();

App.ApplicationController = Ember.Controller.extend({
  appName: 'Snomed Search'
});

App.Router.map(function() {
  this.route("index", {path: "/"});
});


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
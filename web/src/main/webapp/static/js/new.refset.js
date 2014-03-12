MyApp = Ember.Application.create({
  rootElement: '#refset',
  LOG_TRANSITIONS: true
});

MyApp.Router.map(function() {
  this.route('index', {path: '/'});
});
 
MyApp.ApplicationRoute = Ember.Route.extend({
  actions: {
    click: function(concept){
      $("#concept-title").text(concept.title);
      $("#concept-title").attr('href', '/version/1/concept/' + concept.id);
      $("#concept-id").val(concept.id);
      var selected = $(".toggle-find-concept.selected");
      var notSelected = $(".toggle-find-concept.not-selected");
      selected.attr('class', 'toggle-find-concept selected active');
      notSelected.attr('class', 'toggle-find-concept not-selected inactive');
      $('#textSearchModal').modal('hide');
      console.log('handled click');
      return false;
    },
  }  
});  
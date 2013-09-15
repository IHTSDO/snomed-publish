App = Ember.Application.create();

App.ApplicationController = Ember.Controller.extend({
  appName: 'Snomed Search'
});

App.Router.map(function() {
  this.route("index", {path: "/"});
});

// INDEX
App.IndexRoute = Ember.Route.extend({
//  setupController : function(controller,model){
//    this.controllerFor("searchInput").search("Family");
//  }
});
App.IndexController = Ember.Controller.extend({
  //needs:["searchInputController"]
});

//TEXT SEARCH
App.TextSearchView = Ember.View.extend({
  templateName: 'textSearch'
});

// SEARCH INPUT
App.SearchInputController  = Ember.Controller.extend({  
  query: 'Family',
  search: function(search) {
      var results = App.TextSearch.find(search);
      var _this = this;
      // results is a jquery promise, wait for it to resolve
      // Ember can't resolve it automatically
      results.then(function(results){
        _this.get('controllers.searchResults').set('model', results);
      });
      return false;
  },
  needs: "searchResults"
});

App.SearchInputView = Ember.View.extend({
  templateName: 'searchInput',
  keyUp: function(evt) {
    this.get('controller').send('search', this.get('controller.query'));
  }
});

// SEARCH RESULTS
App.SearchResultsController  = Ember.Controller.extend({
  // This controller could be removed.
  // Ember will auto generate one for you.
});

App.SearchResultsView = Ember.View.extend({
  templateName: 'searchResults'
});


App.SearchResults = Ember.Object.extend({
  total: 0,
  start: 0,
  concepts: Ember.A()
});

App.Concept = Ember.Object.extend({
  id: null,
  title: null,
  active: null,
  effectiveTime: null
});

App.TextSearch = Ember.Object.extend({});

App.TextSearch.reopenClass({
  find: function(searchString){
    return Ember.Deferred.promise(function(p) {
      p.resolve($.getJSON("http://solr.sparklingideas.co.uk/solr/concept/select?q=title:" + searchString + "&wt=json&indent=true&json.wrf=?")
        .then(function(solr) {
            var returned = App.SearchResults.create();
            returned.set('total', solr.response.numFound);
            returned.set('start', solr.response.start);
            var concepts = Ember.A();
            solr.response.docs.forEach(function (doc) {
              var concept = App.Concept.create();
              concept.id = doc.id;
              concept.title = doc.title;
              concept.active = doc.active;
              concept.effectiveTime= doc.effectiveTime;
              concepts.pushObject(concept);
            });
            returned.set('concepts',concepts);
            return returned;
        }) //then
      );//resolve
    });//deferred promise
  }//find
});//reopen
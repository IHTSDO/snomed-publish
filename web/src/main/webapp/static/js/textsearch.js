Handlebars.registerHelper('times', function(n, block) {
    var accum = '';
    for(var i = 0; i < n; ++i)
        accum += block.fn(i);
    return accum;
});

// TEXT SEARCH
// -----------

App.TextSearchView = Ember.View.extend({
  templateName: 'textSearch'
});

App.TextSearchController = Ember.Controller.extend({
  actions:{
    search: function(search) {
      var results = App.TextSearchController.find(search);
      var _this = this;
      // results is a jquery promise, wait for it to resolve
      // Ember can't resolve it automatically
      results.then(function(results){
        _this.get('controllers.searchResults').set('model', results);
      });
      return false;
    }
  },
  needs: "searchResults"
});

App.TextSearchController.reopenClass({
  pagesize: 10,
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
              concept.ontologyId = doc.ontology_id;
              concept.title = doc.title;
              concept.active = doc.active;
              concept.effectiveTime= doc.effectiveTime;
              concepts.pushObject(concept);
            });
            var pages = returned.get('total') / 10;
            returned.pages = Ember.A();
            for (i=1; i <= pages; i++){
              returned.pages.pushObject(i);
            }
            returned.set('concepts',concepts);
            return returned;
        }) //then
      );//resolve
    });//deferred promise
  }//find
});//reopen


// SEARCH INPUT
// ------------

App.SearchInputController  = Ember.Controller.extend({  
  query: null
});

App.SearchInputView = Ember.View.extend({
  templateName: 'searchInput',
  keyUp: function(evt) {
    this.get('controller').send('search', this.get('controller.query'));
  },
  needs: "textSearch"
});


// SEARCH RESULTS
// --------------

App.SearchResultsController  = Ember.Controller.extend({});

App.SearchResultsView = Ember.View.extend({
  templateName: 'searchResults'
});

App.SearchResults = Ember.Object.extend({
  total: 0,
  pages: null,
  concepts: Ember.A()
});

App.Concept = Ember.Object.extend({
  id: null,
  ontologyId: null,
  title: null,
  active: null,
  effectiveTime: null,
  url: function(){
    return "/ontology/" + this.ontologyId + "/concept/" + this.id;
  }.property()
});

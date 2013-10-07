
// TEXT SEARCH

  MyApp.register('controller:pages', MyApp.PagesController, {singleton: false});
  MyApp.register('controller:searchInput', MyApp.SearchInputController, {singleton: false});
  MyApp.register('controller:searchResults', MyApp.SearchResultsController, {singleton: false});

  MyApp.TextSearchView = Ember.View.extend({
    templateName: 'textSearch'
  });

  MyApp.TextSearchController = Ember.ObjectController.extend({
    searchResults: null,
    actions:{
      search: function(search) {
        
        if (search===""){
          this.get('controllers.searchResults').set('model', null);
          return false;
        }
        
        console.log('received search event "' + search + '"');
        var results = MyApp.TextSearchController.find(search, 1, this.get('controllers.pages').get('pageSize'));
        var _this = this;
        // results is a jquery promise, wait for it to resolve
        // Ember can't resolve it automatically
        results.then(function(results){
          _this.set('searchResults', results);
          _this.get('controllers.pages').set('totalItems', results.total);
          _this.get('controllers.pages').buildPages(1, 1);
        });
        return false;
      },
      pageRequest: function(page){
        console.log('received page request for page ' + page.get('index'));
        //console.log('page ' + page.get('display'));
        var pagesController = this.get('controllers.pages');
        var searchInputController = this.get('controllers.searchInput');
        var _this = this;
        console.log('disabling page ' + pagesController.get('currentPage.index'));
        pagesController.get('currentPage').set('active', false);
        var results = MyApp.TextSearchController.find(
            searchInputController.get('query'), 
            page.index, 
            pagesController.get('pageSize'));
        results.then(function(results){
          _this.get('controllers.searchResults').set('model', results);
        });
        console.log('setting current page to ' + page.get('index') + ' and making active');
        pagesController.set('currentPage', page);
        page.set('active', true);
        return false;
      }
    },
    needs: ["searchResults", "searchInput", "pages"]
  });

  MyApp.TextSearchController.reopenClass({
    find: function(searchString, pageIndex, pageSize){
      return Ember.Deferred.promise(function(p) {
        var startIndex = (pageIndex - 1) * pageSize;
        p.resolve($.getJSON("http://solr.snomedtools.com/solr/concept/select?q=title:" + searchString + "&start=" + startIndex + "&rows=" + pageSize + "&wt=json&indent=true&json.wrf=?")
          .then(function(solr) {
              var returned = MyApp.SearchResults.create();
              returned.set('total', solr.response.numFound);
              returned.set('start', solr.response.start);
              var concepts = Ember.A();
              solr.response.docs.forEach(function (doc) {
                var concept = MyApp.Concept.create();
                concept.id = doc.id;
                concept.ontologyId = doc.ontology_id;
                concept.title = doc.title;
                concept.active = doc.active;
                concept.effectiveTime= doc.effectiveTime;
                concepts.pushObject(concept);
              });
              returned.set('concepts',concepts);
              
              console.log('returned ' + returned.get('total') + ' records');
              return returned;
          }) //then
        );//resolve
      });//deferred promise
    },//find
  });//reopen



// (TEXT) SEARCH INPUT
  //IS this controller even used?
  MyApp.SearchInputController  = Ember.ObjectController.extend({  
    query: null,
    needs: ["textSearch", "searchInput", "searchResults"]
  });

  MyApp.SearchInputView = Ember.View.extend({
    templateName: 'searchInput',
    keyUp: function(evt) {
      //looks like current controller is textSearchController?
      //Or maybe it bubbles to it?
      this.get('controller').send('search', this.get('controller.query'));
    },
    needs: ["textSearch", "searchInput", "searchResults"]
  });



// (TEXT) SEARCH RESULTS

  MyApp.SearchResultsController  = Ember.ObjectController.extend({
    needs: ['pages', 'textSearch'],
    searchResults: function(){
      return this.get('controllers.textSearch.searchResults');
    }.property()
  });

  MyApp.SearchResultsView = Ember.View.extend({
    templateName: 'searchResults'
  });

  MyApp.SearchResults = Ember.Object.extend({
    total: 0,
    concepts: Ember.A()
  });

  MyApp.Concept = Ember.Object.extend({
    stylingClass: function(){
      if (this.get('active')){
        return 'active';
      }
      else{
        return 'inactive';
      }
    }.property('active'),
    id: null,
    ontologyId: null,
    title: null,
    active: null,
    effectiveTime: null,
    url: function(){
      return "http://browser.snomedtools.com/version/1/concept/" + this.get('id') 
    }.property('id')
  });




// PAGES 

  MyApp.Page = Ember.Object.extend({
    active: false,
    index: null
  });

  MyApp.PagesController = Ember.Controller.extend({
    model: null,
    selection: null,
    currentPage: null,
    maxPageIndexesShown: 10,
    pageSize: 8,
    totalItems: function(){
      console.log('in totalItems now: ' + this.get('controllers.textSearch.searchResults.total'));
      return this.get('controllers.textSearch.searchResults.total');
    },
    numberOfPages: function(){
      return Math.ceil(this.get('controllers.textSearch.searchResults.total') / this.get('pageSize'));
    }.property('totalItems'),

    displayWidthStyling: function(){
      var itemWidth = 4;
       return 'width: ' + ((4 * itemWidth) + (this.get('model.length') * itemWidth)) + 'em;';
    }.property('model'),
    needs: ["searchResults", "textSearch"],
    actions:{
      firstPage: function(){
        console.log('First page'); 
        this.buildPages(1, 1);
        this.send('pageRequest', this.get('model.firstObject'));
      },
      previousPage: function(){
        console.log('previous page');
        var page = null;
        if ((this.get('currentPage.index') - 1) % this.get('maxPageIndexesShown') == 0){
          //Assertion: we are at the first displayed page index
          console.log('we are at the first displayed page index');
          if (this.get('currentPage.index') == 1){
            //assertion: we are at the beginning of the list of pages, so loop around to the back.
            console.log('we are at the beginning of the list of pages, so loop around to the back');
            console.log('last page');
            var numberOfPagesOnLastSet = this.get('numberOfPages') % this.get('maxPageIndexesShown');
            console.log('number of pages on last view ' + numberOfPagesOnLastSet);
            console.log('calling buildPages(' + (this.get('numberOfPages') - numberOfPagesOnLastSet) + ', ' + this.get('numberOfPages') + ')');
            this.buildPages(this.get('numberOfPages') - numberOfPagesOnLastSet + 1, numberOfPagesOnLastSet);
            page = this.get('model.lastObject');
          }
          else{
            //assertion: there are more page indexes to the left / below to display
            console.log('there are more page indexes to the left / below to display');
            var index = this.get('currentPage.index') - this.get('maxPageIndexesShown');
            console.log('buildPages(' + index + ', ' + this.get('maxPageIndexesShown') + ')');
            this.buildPages(index, this.get('maxPageIndexesShown'));
            page = this.get('model.lastObject');
          }
        }else{
          var index = ((this.get('currentPage.index') - 1) % this.get('maxPageIndexesShown')) - 1;
          console.log('we are not at the beginning of the page list');
          console.log('selecting model.objectAt (' + index + ') for page');
          page = this.get('model').objectAt(index);
        }
        console.log('change page to index ' + page.get('index'));
        this.send('pageRequest', page);
      },
      nextPage: function(){
        var page = null;
        if ((this.get('currentPage.index') % this.get('maxPageIndexesShown') == 0)
            || (this.get('currentPage.index') == this.get('numberOfPages'))){
          //Assertion: we are at the last displayed page index
          console.log('we are at the last displayed page index');
          if (this.get('currentPage.index') == this.get('numberOfPages')){
            //assertion: we are at the end of the list of pages, so loop around.
            console.log('we are at the end of the list of pages, so loop around.');
            console.log('First page');
            console.log('buildpages(1, 1)');
            this.buildPages(1, 1);
            page = this.get('model.firstObject');
          }
          else{
            //Assertion: we are at the last displayed page index
            //assertion: there are more page indexes to display
            console.log('there are more page indexes to display');
            var index = this.get('currentPage.index') + 1;
            console.log('buildPages(' + index + ', 1)');
            this.buildPages(index, 1);
            page = this.get('model.firstObject');
          }
        }else{
          console.log('we are not at the end of the page list');
          var index = this.get('currentPage.index') % this.get('maxPageIndexesShown');
          console.log('getting page: model.objectAt(' + index + ')');
          page = this.get('model').objectAt(index);
        }
        
        this.send('pageRequest', page);
      },
      lastPage: function(){
        console.log('last page');
        var numberOfPagesOnLastSet = this.get('numberOfPages') % this.get('maxPageIndexesShown');
        console.log('number of pages on last view ' + numberOfPagesOnLastSet);
        console.log('calling buildPages(' + (this.get('numberOfPages') - numberOfPagesOnLastSet) + ', ' + this.get('numberOfPages') + ')');
        this.buildPages(this.get('numberOfPages') - numberOfPagesOnLastSet + 1, numberOfPagesOnLastSet);
        this.send('pageRequest', this.get('model.lastObject'));
      }
    },

    shouldDisplayNavigation: function(){
      var model = this.get('model'); 
      if (model != null){
        if (model.length > 1) return true;
      }else{
        return false;
      }
    }.property('model'),
    
    buildPages: function(startAtPageIndex, activeIndex){
      //Build Page Index
      this.set('model', Ember.A());
      var numberOfPages = this.get('numberOfPages');
      var maxIndex = startAtPageIndex + this.get('maxPageIndexesShown') - 1;
      console.log('building pages from ' + startAtPageIndex + ' to max ' + maxIndex + ' out of total ' + numberOfPages);
      console.log('number of pages are ' + numberOfPages);
      for (i=startAtPageIndex; i <= numberOfPages && i <= maxIndex; i++){
        var page = MyApp.Page.create();
        page.active = false;
        page.index = i;
        this.get('model').pushObject(page);
      }
      console.log('setting current page to ' + (activeIndex) + ' and making active');
      this.set('currentPage', this.get('model').objectAt(activeIndex - 1));
      this.get('currentPage').set('active', true);
    }
  });

MyApp.Page = Ember.Object.extend({
  active: false,
  index: null
});


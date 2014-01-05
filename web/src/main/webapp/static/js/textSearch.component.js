
// TEXT SEARCH

  //MyApp.register('controller:textSearch', MyApp.TextSearchController, {singleton: false});
  //MyApp.register('controller:pages', MyApp.PagesController, {singleton: false});
  //MyApp.register('controller:searchInput', MyApp.SearchInputController, {singleton: false});
  //MyApp.register('controller:searchResults', MyApp.SearchResultsController, {singleton: false});

  MyApp.TextSearchView = Ember.View.extend({
    templateName: 'textSearch'
  });

  MyApp.TextSearchComponent = Ember.Component.extend({
    //-------------------------------------------
    // Ember Properites
    //-------------------------------------------
    content: Ember.ArrayController.create(),    

    //-------------------------------------------
    // Instance Properties
    //-------------------------------------------    
    query: null,
    searchResults: null,
    pages: null,
    currentPage: null,
    maxPageIndexesShown: 5,
    pageSize: 8,
    needs: "searchResults",
    actions:{
      
      //SEND CONCEPT TO PARENT
      click: function(concept){
        this.sendAction('action', concept);
      },

      search: function(search) {
        if (search===""){
          this.get('controllers.searchResults').set('model', null);
          return false;
        }
        console.log('received search event "' + search + '"');
        console.log('pagesize is ' + this.get('pageSize'));
        var results = MyApp.TextSearchComponent.find(search, 1, this.get('pageSize'));
        var _this = this;
        // results is a jquery promise, wait for it to resolve
        // Ember can't resolve it automatically
        results.then(function(results){
          console.log('query returned with ' + results.total + ' results');
          _this.set('searchResults', results);
          _this.buildPages(1, 1);
          console.log('blah3');
        });
        return false;
      },
      pageRequest: function(page){
        console.log('received page request for page ' + page.get('index'));
        //console.log('page ' + page.get('display'));
        var _this = this;
        console.log('disabling page ' + this.get('currentPage.index'));
        this.get('currentPage').set('active', false);
        var results = MyApp.TextSearchComponent.find(
            this.get('query'), 
            page.index, 
            this.get('pageSize'));
        results.then(function(results){
          _this.set('searchResults', results);
        });
        console.log('setting current page to ' + page.get('index') + ' and making active');
        this.set('currentPage', page);
        page.set('active', true);
        return false;
      },
      firstPage: function(){
        console.log('First page'); 
        this.buildPages(1, 1);
        this.send('pageRequest', this.get('pages.firstObject'));
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
            page = this.get('pages.lastObject');
          }
          else{
            //assertion: there are more page indexes to the left / below to display
            console.log('there are more page indexes to the left / below to display');
            var index = this.get('currentPage.index') - this.get('maxPageIndexesShown');
            console.log('buildPages(' + index + ', ' + this.get('maxPageIndexesShown') + ')');
            this.buildPages(index, this.get('maxPageIndexesShown'));
            page = this.get('pages.lastObject');
          }
        }else{
          var index = ((this.get('currentPage.index') - 1) % this.get('maxPageIndexesShown')) - 1;
          console.log('we are not at the beginning of the page list');
          console.log('selecting pages.objectAt (' + index + ') for page');
          page = this.get('pages').objectAt(index);
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
            page = this.get('pages.firstObject');
          }
          else{
            //Assertion: we are at the last displayed page index
            //assertion: there are more page indexes to display
            console.log('there are more page indexes to display');
            var index = this.get('currentPage.index') + 1;
            console.log('buildPages(' + index + ', 1)');
            this.buildPages(index, 1);
            page = this.get('pages.firstObject');
          }
        }else{
          console.log('we are not at the end of the page list');
          var index = this.get('currentPage.index') % this.get('maxPageIndexesShown');
          console.log('getting page: pagespages.objectAt(' + index + ')');
          page = this.get('pages').objectAt(index);
        }
        
        this.send('pageRequest', page);
      },
      lastPage: function(){
        console.log('last page');
        var numberOfPagesOnLastSet = this.get('numberOfPages') % this.get('maxPageIndexesShown');
        console.log('number of pages on last view ' + numberOfPagesOnLastSet);
        console.log('calling buildPages(' + (this.get('numberOfPages') - numberOfPagesOnLastSet) + ', ' + this.get('numberOfPages') + ')');
        this.buildPages(this.get('numberOfPages') - numberOfPagesOnLastSet + 1, numberOfPagesOnLastSet);
        this.send('pageRequest', this.get('pages.lastObject'));
      }
    },


    totalPageItems: function(){
      return this.get('searchResults.total');
    }.property('searchResults'),
    
    numberOfPages: function(){
      return Math.ceil(this.get('searchResults.total') / this.get('pageSize'));
    }.property('searchResults'),

    buildPages: function(startAtPageIndex, activeIndex){
      //Build Page Index
      console.log('Building pages with startAtPageIndex: ' + startAtPageIndex + ", activeIndex: " + activeIndex);
      var newPages = Ember.A();
      this.set('pages', newPages);
      var numberOfPages = this.get('numberOfPages');
      var maxIndex = startAtPageIndex + this.get('maxPageIndexesShown') - 1;
      console.log('building pages from ' + startAtPageIndex + ' to max ' + maxIndex + ' out of total ' + numberOfPages);
      console.log('number of pages are ' + numberOfPages);
      for (i=startAtPageIndex; i <= numberOfPages && i <= maxIndex; i++){
        var page = MyApp.Page.create();
        page.active = false;
        page.index = i;
        newPages.pushObject(page);
      }
      console.log('setting current page to ' + (activeIndex) + ' and making active');
      this.set('currentPage', newPages.objectAt(activeIndex - 1));
      this.get('currentPage').set('active', true);
    },
    shouldDisplayNavigation: function(){
      var mypages = this.get('pages'); 
      if (mypages != null){
        if (mypages.length > 1) return true;
      }else{
        return false;
      }
    }.property('pages'),
    displayWidthStyling: function(){
      var itemWidth = 4;
      return 'width: ' + ((4 * itemWidth) + (this.get('pages.length') * itemWidth)) + 'em;';
    }.property('pages')
  });

  MyApp.TextSearchComponent.reopenClass({
    find: function(searchString, pageIndex, pageSize){
      console.log('find(searchString: \'' + searchString + '\', pageIndex: ' + pageIndex + ', pageSize: ' + pageSize + ')');
      return Ember.Deferred.promise(function(p) {
        var startIndex = (pageIndex - 1) * pageSize;
        p.resolve($.getJSON("https://solr.snomedtools.com/solr/concept/select?q=title:" + searchString + "&start=" + startIndex + "&rows=" + pageSize + "&wt=json&indent=true&json.wrf=?")
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



MyApp.TextSearchModalView = Ember.View.extend({
  templateName: 'textSearchModal'
});


// (TEXT) SEARCH INPUT
  MyApp.SearchInputView = Ember.View.extend({
    templateName: 'searchInput',
    keyUp: function(evt) {
      //looks like current controller is textSearchController?
      //Or maybe it bubbles to it?
      this.get('controller').send('search', this.get('controller.query'));
    }
  });



// (TEXT) SEARCH RESULTS

  MyApp.SearchResultsController = Ember.ObjectController.extend({
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
    index: null,
  });

  MyApp.PagesView = Ember.View.extend({
    templateName: 'pages', 
    
  })






module.exports = function(app, passport, config) {

	/* GET home page. */

    app.namespace(config.kibana.prefix, function() {

    	app.get('/', isAuthenticated, function(req, res, next) {
    	  res.redirect(config.kibana.rootURL);
    	});
    
    	app.get('/dashboard', isAuthenticated,  function(req, res) {
    	    res.redirect(config.kibana.dashboardURL);
    	});
    
    	app.get('/discover', isAuthenticated, function(req, res) {
    	    res.redirect(config.kibana.discoverURL);
    	});
    
    	app.get('/visualize', isAuthenticated, function(req, res) {
    	    res.redirect(config.kibana.visualizeURL);
    	});
    
    	app.get('/settings', isAuthenticated, function(req, res) {
    	    res.redirect(config.kibana.settingsURL);
    	});
    
    	app.get('/login', function(req, res) {
            if(req.isAuthenticated())
                res.redirect(config.kibana.rootURL);
    	    res.render('login');
    	});
    
    	// =============================================================================
    	// REGISTER USING GOOGLE
    	// =============================================================================
    
        // send to google to do the authentication
        app.get('/auth/google', 
        	passport.authenticate('google', 
        		{ scope : ['profile', 'email'] }
        ));
        // the callback after google has authenticated the user
        app.get(config.googleAuth.callbackURL,
        	passport.authenticate('google', {
            	successRedirect : '/',
            	failureRedirect : '/login'
        }));
    
        app.get('/connect/google', 
        	passport.authorize('google', 
        		{ scope : ['profile', 'email'] }
        ));
    
        // the callback after google has authorized the user
        app.get('/connect/google/callback',
            passport.authorize('google', {
                successRedirect : '/',
                failureRedirect : '/login'
        }));
    
    });
};

function isAuthenticated(req, res, next) {
   if (req.isAuthenticated())
         return next();
    res.redirect('/kibana/login');
}

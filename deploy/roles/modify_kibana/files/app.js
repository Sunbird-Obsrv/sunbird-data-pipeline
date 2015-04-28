var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var requestLogger = require('./lib/requestLogger');
var auth = require('./lib/auth');
var appHeaders = require('./lib/appHeaders');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var compression = require('compression');
var session = require( 'express-session' );
var config = require('./config');
var passport = require( 'passport' );
var GoogleStrategy = require('passport-google-oauth2').Strategy


var GOOGLE_CLIENT_ID = '149422338782-gp602lvp42iig3cvovi7i6i4rp3dr9o5.apps.googleusercontent.com',
    GOOGLE_CLIENT_SECRET = 'DiFwTGwVGcxgiTR6eWlntRN9',
    redirect = 'http://platform.ekstep.org/ecosystem/oauth2callback';

passport.serializeUser(function(user, done) {
  done(null, user);
});

passport.deserializeUser(function(obj, done) {
  done(null, obj);
});

passport.use(new GoogleStrategy({
    clientID:     GOOGLE_CLIENT_ID,
    clientSecret: GOOGLE_CLIENT_SECRET,
    callbackURL: redirect,
    passReqToCallback   : true
  },
  function(request, accessToken, refreshToken, profile, done) {
    if (profile.email.indexOf("sahajsoft.com") == -1 && profile.email.indexOf("ekstep.org") == -1  && profile.email.indexOf("ilimi.in") == -1 ){
      return done(new Error("Access is restricted for your domain."));
    }
    return done(null, profile);
  }
));

var routes = require('./routes/index');
var proxy = require('./routes/proxy');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.set('x-powered-by', false);

app.use(requestLogger());
app.use(auth());
app.use(appHeaders());
app.use(favicon(path.join(config.public_folder, 'styles', 'theme', 'elk.ico')));

if (app.get('env') === 'development') {
  require('./dev')(app);
}

// The proxy must be set up before all the other middleware.
// TODO: WE might want to move the middleware to each of the individual routes
// so we don't have weird conflicts in the future.
app.use('/elasticsearch', proxy);
app.use('/enforcer', require('./lib/enforce'));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(compression());
app.use(session({
  secret: 'random secret'
}))
app.use( passport.initialize());
app.use( passport.session());
app.use(ensureAuthenticated)
app.use(express.static(config.public_folder));
if (config.external_plugins_folder) app.use('/plugins', express.static(config.external_plugins_folder));

app.get('/login', function(req, res){
  res.render('login', { user: req.user });
});

app.get('/auth/google', passport.authenticate('google', { scope: [
      'https://www.googleapis.com/auth/userinfo.email',
      'https://www.googleapis.com/auth/userinfo.profile'
    ],accessType: 'offline'
}));

app.get( '/oauth2callback',
      passport.authenticate( 'google', {
        successRedirect: '/ecosystem/',
        failureRedirect: '/ecosystem/login'
}));

app.get('/logout', function(req, res){
  req.logout();
  res.redirect('/ecosystem/login');
});

app.use('/', routes);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function (err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function (err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


function ensureAuthenticated(req, res, next) {
  if (req.isAuthenticated()) { return next(); }
  //throw new Error(req.isAuthenticated());
  if (req.path === "/login" || req.path.indexOf("/auth") > -1  || req.path.indexOf('/oauth2callback') > -1 ) { return next(); }
  res.redirect('/ecosystem/login');
}

module.exports = app;

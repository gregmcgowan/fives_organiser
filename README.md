# fives_organiser
![example workflow name](https://github.com/gregmcgowan/fives_organiser/workflows/Android%20CI/badge.svg)

A small app to organise weekly football games.

When I first learned Android development I made an app which helped with some of the tedious bits of organising a weekly football five-a-side game (keep track of what players were available to play, keeping win ratios to balance teams) it was very rudimentary but a great learning experience.

Now I am attempting to bring that back but with newer Android technologies such as Compose.

As such it is very much a work in progress and a lot of things won't work and look terrible but hopefully at somepoint it will be a polished fully functioning app!


## Setup
The app uses Crashlytics and Firebase Firestore so if you try to build without any changes you will get missing a `google-services.json` file build error.

If you want to build and run the app, you need to obtain your own copy from Firebase:

1. Create a project on [Firebase console](https://console.firebase.google.com/) using the package name `com.gregmcgowan.fivesorganiser`. Refer to [this guide](https://firebase.google.com/docs/android/setup) for further details.
2. Follow the instructions, download the `google-services.json` file and put it in the `app` folder. 
3. Hopefully that is it, the app should build and run!

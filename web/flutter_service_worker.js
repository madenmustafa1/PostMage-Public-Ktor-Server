'use strict';
const MANIFEST = 'flutter-app-manifest';
const TEMP = 'flutter-temp-cache';
const CACHE_NAME = 'flutter-app-cache';

const RESOURCES = {"version.json": "1a8d89cfbb2347c0edf43d81cf4009d6",
"index.html": "6a45880faa295b9b7107808043141bfd",
"/": "6a45880faa295b9b7107808043141bfd",
"main.dart.js": "aa6951d2098a4cdc784ff13119c0430f",
"flutter.js": "c71a09214cb6f5f8996a531350400a9a",
"icons/app_icon.png": "bda364aa5efe01d0be683063d869a9de",
"icons/app_icon_white_transparent.png": "8199beb79f1955c7b2c6c4f19abdfafd",
"manifest.json": "bf94350fe4c7e8a710ce007ced3e82a8",
"assets/AssetManifest.json": "e6b76f14f3e4ea39459ccabbfa1c3ac2",
"assets/NOTICES": "71575f94ee41c830b3c881aaa5e47078",
"assets/FontManifest.json": "e25c505507e2d3ccf9188f95147c58df",
"assets/AssetManifest.bin.json": "3d3d72de570a774baec5ec5117ae807b",
"assets/packages/line_icons/lib/assets/fonts/LineIcons.ttf": "23621397bc1906a79180a918e98f35b2",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "a6b82c39c7b13fc035050c9c2a37db10",
"assets/packages/font_awesome_flutter/lib/fonts/fa-solid-900.ttf": "b5dd63def2dc3e4259a9c9e43a1fd40c",
"assets/packages/font_awesome_flutter/lib/fonts/fa-regular-400.ttf": "f28f323790aee5a35a64a62c8a77273e",
"assets/packages/font_awesome_flutter/lib/fonts/fa-brands-400.ttf": "f7ad08bb38720122fd4dd4fdf75088fa",
"assets/packages/flutter_image_compress_web/assets/pica.min.js": "6208ed6419908c4b04382adc8a3053a2",
"assets/packages/fluttertoast/assets/toastify.js": "56e2c9cedd97f10e7e5f1cebd85d53e3",
"assets/packages/fluttertoast/assets/toastify.css": "a85675050054f179444bc5ad70ffc635",
"assets/shaders/ink_sparkle.frag": "ecc85a2e95f5e9f53123dcaf8cb9b6ce",
"assets/AssetManifest.bin": "807c1c34d2e33287060488256058019e",
"assets/fonts/MaterialIcons-Regular.otf": "5ab25fc040cae73163fecd756959d815",
"assets/assets/strings/app/es-ES.json": "119733cca4bfe8d9fdfac3be588f2766",
"assets/assets/strings/app/tr-TR.json": "ac329ceabe21915cd54e5873ecb16927",
"assets/assets/strings/app/de-DE.json": "2e5af3b2dd7f84875d1c09eeed19508b",
"assets/assets/strings/app/ru-RU.json": "ee2bc0ca4adfe45d4343779923d32130",
"assets/assets/strings/app/en-US.json": "ed22e07dd5ac26601f0b4acd0722dae3",
"assets/assets/strings/versions.json": "7e1a0f0ff21bebb10980276a8f6b4344",
"assets/assets/images/onboarding0.png": "2df33040c11b48d80e0a8b91716c3f1a",
"assets/assets/images/15_hashtag.png": "e6b49be24ea2f654bc7fd7fc23f35d7b",
"assets/assets/images/onboarding1.png": "bbb74d46c7879bd14c809cb917ddb08e",
"assets/assets/images/1_hashtag.png": "c596d33160bc47981e7cae6211bc3c37",
"assets/assets/images/app_icon_transparent.png": "be9d7ba7720540c59a5317c4060d33e9",
"assets/assets/images/onboarding2.png": "b2428bfb68d5a758e950c3d8c975739a",
"assets/assets/images/8_hashtag.png": "11245bedb58317d464effcbf1e72762b",
"assets/assets/images/19_hashtag.png": "cdf034c6925658d44fbff8e68f10c0c6",
"assets/assets/images/qr_code_icon.png": "be9d7ba7720540c59a5317c4060d33e9",
"assets/assets/images/10_hashtag.png": "2ebdac0ba9ae50c368d7f8c4be6d7291",
"assets/assets/images/app_icon.png": "bda364aa5efe01d0be683063d869a9de",
"assets/assets/images/4_hashtag.png": "37c6af5ccebde38800fac5974916f94c",
"assets/assets/images/step_4.png": "f654d82ff3e65a5e4657033b8db08424",
"assets/assets/images/7_hashtag.png": "7c1a9c2d8b5ede9eca5029fe7adb4658",
"assets/assets/images/web/quality_size.png": "0a561a5501c72b92c00e46b4f094da4a",
"assets/assets/images/web/post_photo.jpg": "8e205dd25cf1581501a3c1814ec0c3a8",
"assets/assets/images/web/doodle_plane.png": "47a5213f069a6f295349b9cda28962b7",
"assets/assets/images/13_hashtag.png": "3a654e9640ff6a3c88a1437e3b0a0827",
"assets/assets/images/step_2.png": "152f067007534f6040c04f3b547ca863",
"assets/assets/images/step_3.png": "f18a939994e954db33884dd20325311c",
"assets/assets/images/2_hashtag.png": "91dfee0a2b60205de0adf4d4c776d912",
"assets/assets/images/step_1.png": "a6c430397bfbbafe806737d934cda0d8",
"assets/assets/images/16_hashtag.png": "e8976b6b8125be8cf24ba61a68a6ee13",
"assets/assets/images/5_hashtag.png": "c65b5998a081c88acaf152183c34a250",
"assets/assets/images/11_hashtag.png": "f4f1909327872ab3c6cf687a34f9b642",
"assets/assets/images/18_hashtag.png": "2466a63e908356bc8503e284c57449d9",
"assets/assets/images/9_hashtag.png": "cd2e946bf549770de16a7e8142cf4ea0",
"assets/assets/images/14_hashtag.png": "6d2fb1c020b1c6b74b022ad687b952f7",
"assets/assets/images/app_icon_white_transparent.png": "8199beb79f1955c7b2c6c4f19abdfafd",
"assets/assets/images/17_hashtag.png": "c5308577ebd2d929304974a4cd16fea9",
"assets/assets/images/3_hashtag.png": "02f9db74150e5e05f91c7664788fca8d",
"assets/assets/images/12_hashtag.png": "955d1de6d3520a4cd00ff06f48ce1069",
"assets/assets/images/6_hashtag.png": "0edfb0b6d5f453ab489752380d4cda99",
"assets/assets/images/20_hashtag.png": "949161ebe5253bf1629c413432b92ef3",
"assets/assets/lottie/wizard_hand.json": "52ec6c669a8ffff7795d82b333eb5237",
"assets/assets/lottie/wizard.json": "5a5ae56fece7a73400d92279f46e692f",
"assets/assets/lottie/wizard_bye.json": "511084b776dda243df22cedf5268c457",
"assets/assets/lottie/bird.json": "a4b1288af5fcf088a822507c1d0122b4",
"canvaskit/skwasm.js": "445e9e400085faead4493be2224d95aa",
"canvaskit/skwasm.js.symbols": "741d50ffba71f89345996b0aa8426af8",
"canvaskit/canvaskit.js.symbols": "38cba9233b92472a36ff011dc21c2c9f",
"canvaskit/skwasm.wasm": "e42815763c5d05bba43f9d0337fa7d84",
"canvaskit/chromium/canvaskit.js.symbols": "4525682ef039faeb11f24f37436dca06",
"canvaskit/chromium/canvaskit.js": "43787ac5098c648979c27c13c6f804c3",
"canvaskit/chromium/canvaskit.wasm": "f5934e694f12929ed56a671617acd254",
"canvaskit/canvaskit.js": "c86fbd9e7b17accae76e5ad116583dc4",
"canvaskit/canvaskit.wasm": "3d2a2d663e8c5111ac61a46367f751ac",
"canvaskit/skwasm.worker.js": "bfb704a6c714a75da9ef320991e88b03"};
// The application shell files that are downloaded before a service worker can
// start.
const CORE = ["main.dart.js",
"index.html",
"assets/AssetManifest.bin.json",
"assets/FontManifest.json"];

// During install, the TEMP cache is populated with the application shell files.
self.addEventListener("install", (event) => {
  self.skipWaiting();
  return event.waitUntil(
    caches.open(TEMP).then((cache) => {
      return cache.addAll(
        CORE.map((value) => new Request(value, {'cache': 'reload'})));
    })
  );
});
// During activate, the cache is populated with the temp files downloaded in
// install. If this service worker is upgrading from one with a saved
// MANIFEST, then use this to retain unchanged resource files.
self.addEventListener("activate", function(event) {
  return event.waitUntil(async function() {
    try {
      var contentCache = await caches.open(CACHE_NAME);
      var tempCache = await caches.open(TEMP);
      var manifestCache = await caches.open(MANIFEST);
      var manifest = await manifestCache.match('manifest');
      // When there is no prior manifest, clear the entire cache.
      if (!manifest) {
        await caches.delete(CACHE_NAME);
        contentCache = await caches.open(CACHE_NAME);
        for (var request of await tempCache.keys()) {
          var response = await tempCache.match(request);
          await contentCache.put(request, response);
        }
        await caches.delete(TEMP);
        // Save the manifest to make future upgrades efficient.
        await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
        // Claim client to enable caching on first launch
        self.clients.claim();
        return;
      }
      var oldManifest = await manifest.json();
      var origin = self.location.origin;
      for (var request of await contentCache.keys()) {
        var key = request.url.substring(origin.length + 1);
        if (key == "") {
          key = "/";
        }
        // If a resource from the old manifest is not in the new cache, or if
        // the MD5 sum has changed, delete it. Otherwise the resource is left
        // in the cache and can be reused by the new service worker.
        if (!RESOURCES[key] || RESOURCES[key] != oldManifest[key]) {
          await contentCache.delete(request);
        }
      }
      // Populate the cache with the app shell TEMP files, potentially overwriting
      // cache files preserved above.
      for (var request of await tempCache.keys()) {
        var response = await tempCache.match(request);
        await contentCache.put(request, response);
      }
      await caches.delete(TEMP);
      // Save the manifest to make future upgrades efficient.
      await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
      // Claim client to enable caching on first launch
      self.clients.claim();
      return;
    } catch (err) {
      // On an unhandled exception the state of the cache cannot be guaranteed.
      console.error('Failed to upgrade service worker: ' + err);
      await caches.delete(CACHE_NAME);
      await caches.delete(TEMP);
      await caches.delete(MANIFEST);
    }
  }());
});
// The fetch handler redirects requests for RESOURCE files to the service
// worker cache.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== 'GET') {
    return;
  }
  var origin = self.location.origin;
  var key = event.request.url.substring(origin.length + 1);
  // Redirect URLs to the index.html
  if (key.indexOf('?v=') != -1) {
    key = key.split('?v=')[0];
  }
  if (event.request.url == origin || event.request.url.startsWith(origin + '/#') || key == '') {
    key = '/';
  }
  // If the URL is not the RESOURCE list then return to signal that the
  // browser should take over.
  if (!RESOURCES[key]) {
    return;
  }
  // If the URL is the index.html, perform an online-first request.
  if (key == '/') {
    return onlineFirst(event);
  }
  event.respondWith(caches.open(CACHE_NAME)
    .then((cache) =>  {
      return cache.match(event.request).then((response) => {
        // Either respond with the cached resource, or perform a fetch and
        // lazily populate the cache only if the resource was successfully fetched.
        return response || fetch(event.request).then((response) => {
          if (response && Boolean(response.ok)) {
            cache.put(event.request, response.clone());
          }
          return response;
        });
      })
    })
  );
});
self.addEventListener('message', (event) => {
  // SkipWaiting can be used to immediately activate a waiting service worker.
  // This will also require a page refresh triggered by the main worker.
  if (event.data === 'skipWaiting') {
    self.skipWaiting();
    return;
  }
  if (event.data === 'downloadOffline') {
    downloadOffline();
    return;
  }
});
// Download offline will check the RESOURCES for all files not in the cache
// and populate them.
async function downloadOffline() {
  var resources = [];
  var contentCache = await caches.open(CACHE_NAME);
  var currentContent = {};
  for (var request of await contentCache.keys()) {
    var key = request.url.substring(origin.length + 1);
    if (key == "") {
      key = "/";
    }
    currentContent[key] = true;
  }
  for (var resourceKey of Object.keys(RESOURCES)) {
    if (!currentContent[resourceKey]) {
      resources.push(resourceKey);
    }
  }
  return contentCache.addAll(resources);
}
// Attempt to download the resource online before falling back to
// the offline cache.
function onlineFirst(event) {
  return event.respondWith(
    fetch(event.request).then((response) => {
      return caches.open(CACHE_NAME).then((cache) => {
        cache.put(event.request, response.clone());
        return response;
      });
    }).catch((error) => {
      return caches.open(CACHE_NAME).then((cache) => {
        return cache.match(event.request).then((response) => {
          if (response != null) {
            return response;
          }
          throw error;
        });
      });
    })
  );
}

brooklyn-ravello
================
Linking Brooklyn and Ravello.

Required properties:

```
ravello.username
ravello.password
ravello.privateKeyFile
ravello.privateKeyId
```

The privateKeyId value should be the ID given by Ravello to the key contained in privateKeyFile. Find this by making a GET to https://cloud.ravellosystems.com/services/keypairs.

Give these as `-D` parameters to `mvn`, or in brooklyn.properties in format `brooklyn.location.named.ravello.<prop> = <value>`

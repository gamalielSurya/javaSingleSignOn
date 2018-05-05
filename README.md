# javaSingleSignOn

The main purpose is to implement Single Sign On method. Enables a user to log in once and gain access to the multiple web application under the same master domain, without being prompted to log in again.

At the beginning, the process will check 2 Cookies on client's browser, i.e. SID Cookie and USER Cookie. If both are not available, the process will create a new Session. And if both cookies are available, process will continue to retrieve session record from Database by the value of SID Cookie.

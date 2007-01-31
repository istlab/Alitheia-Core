#!/usr/bin/perl

#convert passwords hashed in base64 into their hex form

use MIME::Base64 qw();
use bytes;
my $md5 = MIME::Base64::decode($ARGV[0]);
$md5 =~ s/(.)/sprintf "%02x",ord($1)/gmse;

print $md5;
print "\n";
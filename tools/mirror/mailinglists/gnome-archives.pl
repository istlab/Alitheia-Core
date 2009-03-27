#!/usr/bin/env perl

# 
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
#  * Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
#  * Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

use strict;
use LWP;
use LWP::Simple qw(getstore);
use File::Copy;

#Mail archive top level page
my $INITURL   = "http://mail.gnome.org/archives/";

#Local path to save downloaded messages
my $LOCALPATH = "archive";

#Program to use from decompressing archive
my $CMD = "zcat";

my $browser = LWP::UserAgent->new;
my @headers =
  ( 'User-Agent' =>
'Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en) AppleWebKit/523.12.2 (KHTML, like Gecko) Version/3.0.4 Safari/523.12.2'
  );

#Get a list of all mailing list subpages
my $initpage = $browser->get( $INITURL, @headers );
my @listurls = $initpage->content() =~ m/<li><a href=\"(.*)\">.*<\/a><\/li>/g;

print STDERR $#listurls . " list archive URLs collected\n";

mkdir $LOCALPATH;

#For each subpage
foreach my $path (@listurls) {
  my $url       = $INITURL . $path;
  my $localpath = $LOCALPATH . "/" . $path;

  print STDERR "Downloading URL " . $url . "\n";

  mkdir $localpath;

  my $listarchive = $browser->get( $url, @headers );
  my @monthsum =
    $listarchive->content() =~
    m/<td><A href=\"(.*)\">\[ Gzip.*Text.*\]<\/a><\/td>/gi;

  foreach my $filename (@monthsum) {
    my $file = $url . "/" . $filename;
    $filename = $localpath . $filename;

    if ( -e $filename ) {
      print STDERR "  File " . $filename . " exists, skipping \n";
    }
    else {
      print STDERR "  Downloading file " . $file . "\n";
      getstore( $file, $filename );
    }
    
    #Prevent perl from complaining in cases where 
    #gzcat is not installed in a known path.
    $ENV{'PATH'} =~ /(.*)/; $ENV{'PATH'} = $1;
    open ARCH, "$CMD $filename|" or die "Cannot open $filename: $!";

    my $start_mail = 0, my $msgid, my $tmp;
    
    while (<ARCH>) {  
      if (m/^From .*@.*$/) {
        
        if ( $start_mail eq '1' ) {    #Tmp file is open from previous email
          close TMP;
          move( $tmp, $localpath . $msgid );
        }
        
        $tmp = $localpath . "msg.tmp";
        $start_mail = '1';
        open( TMP, ">$tmp" ) or die "Cannot open file $tmp: $!";
        next; #Skip first line, not 
      }

      if (m/^Message-id: \<(.*)\>/i) { $msgid = $1; }

      if ($start_mail) { print TMP $_; }

      if ( eof (ARCH) ) {
        close TMP;
        close ARCH;
        move( $tmp, $localpath . $msgid );
      }
    }
  }
}


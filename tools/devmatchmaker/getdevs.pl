#!/usr/bin/env perl -w

use strict;

my $filename = "developers.txt";
my $user   = "alitheia";
my $passwd = "alitheia";
my $db     = "alitheia";

my $query = "select d.developer_id, d.name, d.username, da.email 
             from developer d left outer join developer_alias da 
                 on d.developer_id=da.developer_id";

if ( -e $filename ) {
  print "File $filename exists\n";
  open DEVS, "< $filename" ;
} else {
  print "Reading from database\n";
  open DEVS, "mysql -u $user -s -p'$passwd' -e'$query' $db" 
    or die "Cound not execute query";
}

my %idname, my %idusername, my %idemails;

while (<DEVS>) {
  next if m/sqo-oss/;  #Alitheia Core generated users
  next if m/alitheia/;
  s/NULL//g;
  (my $devid, my $name, my $username, my $email) = split(/\t/);  
  
  $idname{$devid} = $name;
  $idusername{$devid} = $username;
  
  chomp($email);
  push (@{$idemails{$devid}}, $email);
}

close DEVS;
open OUT, "> developers.txt";

# Output line format is
# real_name|username|email1, email2,...emailn
for my $key (keys %idname){
  my $line = $idname{$key};
  $line = $line."|".$idusername{$key};
  $line = $line."|";
  
  my @emails = @{$idemails{$key}};
  for my $email (@emails) {
    if (defined $email) {
      $line = $line.$email.",";
    }
  }
  
  $line =~ s/,$/\n/;
  next if ($line =~ m/\|\|$/);
  print OUT $line;
}

close OUT;

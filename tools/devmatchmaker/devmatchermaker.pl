#!/usr/bin/env perl

use strict;
use Text::Phonetic;

my $devs = "developers.txt";

open DEVS, "< $devs" or die "Cannot open $devs";
#Load file to mem
my @lines = <DEVS>;

#Hash that stores line numbers that were found matching
my %matched_lines; 
my $linecount = 0;

for my $line (@lines) {
  $linecount++;

  my @lineparts = ( my $name, my $username, my $emails ) = split( /|/, $line );
  my @emails = split( /,/, $emails );

  my $match_weight = 0;
  #Try to match user name
  if ( length($name) > 0 ) {
    my @usernames = potential_usernames($name); 
    
    #Yes, O(n^2) for now
    for my $line_n (@lines) {
      my @lineparts_n = ( my $name_n, my $username_n, my $emails_n ) =
        split( /|/, $line_n );
      my @emails_n = split( /,/, $emails_n );

    }
  }
}

sub potential_usernames {
  my $name = shift;
  my @usernames;
  my @name_parts = split(/ /, $name);
  
  return if ($#name_parts eq 0); 
  
  my $firstname = lc @name_parts[0];
  my $lastname = lc  @name_parts[$#name_parts - 1]; 
  
  #For Richard Matthew Stallman, return
  #stallmanr
  push(@usernames, $lastname.substr($firstname, 0, 1));
  #rstallman
  push(@usernames, substr($firstname, 0, 1).$lastname);
  #richards
  push(@usernames, $firstname.substr($lastname, 0, 1));
  #srichard
  push(@usernames, substr($lastname, 0, 1).$firstname);
  #rms
  if ($#name_parts = 3) {
    push(@usernames, substr($firstname, 0, 1)).substr($name_parts[1], 0, 1).substr($lastname, 0, 1);
  }
  
  return @usernames;
}

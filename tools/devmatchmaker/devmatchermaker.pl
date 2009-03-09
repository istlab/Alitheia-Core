#!/usr/bin/env perl

use strict;
use Data::Dumper;

my $devs = "developers.txt";

open DEVS, "< $devs" or die "Cannot open $devs";
#Load file to mem
my @lines = <DEVS>;

my %emails;
my %usernames;
my %matches;

#populate hashes

my $linecount = 0;

for my $line (@lines) {
  $linecount++;
  chomp($line);

  my @lineparts = (my $name, my $username, my $user_emails) =
      split(/\|/, $line);
  my @user_emails = split(/,/, $user_emails);
  $usernames{$username} = $linecount if $username;
  foreach my $user_mail (@user_emails) {
      $emails{$user_mail}->{linenum} = $linecount;
      $emails{$user_mail}->{rname} = $name if $name;
  }
}

my $i = 0;
my $total = keys(%emails);
while (my ($email, $data) = each(%emails)) {
    $i++;
    print(STDERR "match $i" . '/' . "$total...\n");
    my %match = (
	source => $email,
	target => '',
	matched_with => '',
	distance => 10000,
    );
    my %best_match = %match;
    my @potential_unames = potential_usernames($data->{rname});
    while (my ($username, $u_linenum) = each(%usernames)) {
	my $name_part = (split(/@/, $email))[0];
	my $distance;
	$match{target} = $username;
	for my $potential_uname (@potential_unames) {
	    $match{matched_with} = $potential_uname;
	    if (length($potential_uname) < 4) {
		if ($potential_uname eq $username) {
		    $distance = -1;
		    $match{distance} = - 1;
		    update_match(\%match, \%best_match);
		}
	    } else {
		$match{distance} = levenshtein($potential_uname, $username);
		update_match(\%match, \%best_match);
	    }
	}
	if ($match{distance} > -1) {
	    $match{distance} = levenshtein($name_part, $username);
	    $match{matched_with} = $name_part;
	    update_match(\%match, \%best_match);
	}
	if (!exists($matches{$email})
	    || $matches{$email}->{distance} > $best_match{distance}) {
	    $matches{$email}->{distance} = $best_match{distance};
	    $matches{$email}->{username} = $best_match{target};
	    $matches{$email}->{with} = $best_match{matched_with};
	    $matches{$email}->{rname} = $data->{rname};
	    $matches{$email}->{linenum} = "$data->{linenum}:$u_linenum";
	    # print $email, Dumper $matches{$email};
	}
    }
    # print $email, Dumper $matches{$email};
}

while (my ($email, $match) = each(%matches)) {
    print("email:$email username: $match->{username} matched_with:$match->{with} rname: $match->{rname} distance: $match->{distance} \n");
}


sub update_match {
    my ($match, $best_match) = @_;
    if ($match->{distance} < $best_match->{distance}) {
	while (my ($key, $value) = each(%$match)) {
	    $best_match->{$key} = $match->{$key};
	}
    }
}

# The function expects two string parameters
# Usage: levenshtein( <string1>, <string2> )
#
# Algorithm adopted from here: http://www.merriampark.com/ldperl.htm
 
sub levenshtein {
    my ($a, $b) = @_;
    my ($len1, $len2) = (length $a, length $b);
 
    return $len2 if ($len1 == 0);
    return $len1 if ($len2 == 0);
 
    my %d;
 
    for (my $i = 0; $i <= $len1; ++$i) {
	for (my $j = 0; $j <= $len2; ++$j) {
	    $d{$i}{$j} = 0;
	    $d{0}{$j} = $j;
	}
 
	$d{$i}{0} = $i;
   }
 
    # Populate arrays of characters to compare
    my @ar1 = split(//, $a);
    my @ar2 = split(//, $b);
 
    for (my $i = 1; $i <= $len1; ++$i) {
	for (my $j = 1; $j <= $len2; ++$j) {
	    my $cost = ($ar1[ $i - 1 ] eq $ar2[ $j - 1 ]) ? 0 : 1;
 
	    my $min1 = $d{$i - 1}{$j} + 1;
	    my $min2 = $d{$i}{$j - 1} + 1;
	    my $min3 = $d{$i - 1}{$j - 1} + $cost;
 
	    if ($min1 <= $min2 && $min1 <= $min3) {
		$d{$i}{$j} = $min1;
	    }
	    elsif ($min2 <= $min1 && $min2 <= $min3) {
		$d{$i}{$j} = $min2;
	    } else {
		$d{$i}{$j} = $min3;
	   }
	}
    }
    
    return $d{$len1}{$len2};
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

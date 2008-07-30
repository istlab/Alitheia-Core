/^\*\ \^TO/{
  s/^\*\ \^TO\(.*\)$/\1,/; # get email address
  N;                       # append next line
  s/\n\|\ //;              # remove spaces and line feeds
  s/\(^.*\)\/$/\1/;        # remove trailing slash
  s/\(^.*\),.*\/\(.*\)$/   <mailinglist name="\2" email="\1"\/>/p;   # split into xml entry
}

{
  # match plugin line
 /<tr class="edit"/ { # this line has the hash code
  N;                  # append next line (Registation status)
  N;				  # append plugin name
  s/\n\|^[\ ]*//g;    # remove line feeds and starting spaces
  s/>[\ ]*<//g;       # remove spaces between tags
  s/^.*value='\(.*\)'.*&nbsp;\(.*\)<.*trans\">\(.*\)<.*$/\3,\2,\1/
  p;
  }
}

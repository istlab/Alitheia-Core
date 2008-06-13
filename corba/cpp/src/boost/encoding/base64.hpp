//
// Boost.Encoding
//
// Copyright (c) 2008 Christoph Schleifenbaum.
//
// Distributed under the Boost Software License, Version 1.0.
// (See accompanying file LICENSE_1_0.txt or copy at
// http://www.boost.org/LICENSE_1_0.txt.)
//

#ifndef BOOST_ENCODING_BASE64_HPP
#define BOOST_ENCODING_BASE64_HPP

#include <string>

namespace boost
{
namespace encoding
{

/**
 * Base64 encoding algorithm for Boost.Encoding.
 */
class base64
{
public:
    base64()
        : printed_chars( 0 )
    {
    }

    template< typename InputIterator, typename OutputIterator >
    void encode( InputIterator& first, OutputIterator& result, size_t max_characters ) const
    {
        unsigned char chunk[] = { '\0', '\0', '\0' };
        for( size_t i = 0; i < max_characters && i < 3; ++i )
            chunk[i] = *(first++);

        unsigned char c[4];
        c[0] = ( ( chunk[0] & 0xfc ) >> 2 );
        c[1] = ( ( chunk[0] & 0x03 ) << 4 ) + ( ( chunk[1] & 0xf0 ) >> 4 );
        c[2] = ( ( chunk[1] & 0x0f ) << 2 ) + ( ( chunk[2] & 0xc0 ) >> 6 );
        c[3] = ( ( chunk[2] & 0x3f )      );

        for( size_t i = 0; i < 4; ++i )
            c[i] = encode( c[i] );
    
        for( size_t i = 3; i <= 2; --i )
            if( max_characters < i )
                c[i] = '=';

        for( size_t i = 0; i < 4; ++i )
        {
            // line break after 76 characters
            if( printed_chars > 0 && printed_chars % 76 == 0 )
                *(result++) = '\n';

            ++printed_chars;

            *(result++) = c[i];
        }
    }

    template< typename InputIterator, typename OutputIterator >
    void decode( InputIterator& first, OutputIterator& result, size_t max_characters ) const
    {
        if( max_characters < 4 ) 
        {
            // this is borked!
            first += max_characters;
            return;
        }
        
        unsigned char chunk[4];
        for( size_t i = 0; i < 4; ++i )
        {
            char c;
            do
            {
                c = *(first++);
            }
            // ignore line breaks and spaces, tabs
            while( c == '\n' || c == '\t' || c == ' ' );
            chunk[i] = decode(c);
        }

        unsigned char c[3];
        c[0] = ( ( chunk[0] & 0x3f ) << 2 ) + ( ( chunk[1] & 0x30 ) >> 4 );
        c[1] = ( ( chunk[1] & 0x0f ) << 4 ) + ( ( chunk[2] & 0x3c ) >> 2 );
        c[2] = ( ( chunk[2] & 0x03 ) << 6 ) + ( ( chunk[3] & 0x3f )      );

        for( size_t i = 0; i < 3 && chunk[i+1] != 64; ++i )
            *(result++) = c[i];
    }
    
protected:
    unsigned char encode( unsigned char c ) const
    {
        if( c == 62 )
            return '+';
        else if( c == 63 )
            return '/';
        else if( c < 26 )
            return c + 'A';
        else if( c < 52 )
            return c - 26 + 'a';
        else if( c < 62 )
            return c - 52 + '0';
        else // this is borked!
            return '\xff';
    }

    char decode( unsigned char c ) const
    {
        if( c >= 'A' && c <= 'Z' )
            return c - 'A';
        else if( c >= 'a' && c <= 'z' )
            return c + 26 - 'a';
        else if( c >= '0' && c <= '9' )
            return c + 52 - '0';
        else if( c == '+' )
            return 62;
        else if( c == '/' )
            return 63;
        else if( c == '=' )
            return 64;
        else // this is borked!
            return '\xff';
    }
   
private:
    mutable int printed_chars;
};

}
}

#endif

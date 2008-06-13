//
// Boost.Encoding
//
// Copyright (c) 2008 Christoph Schleifenbaum.
//
// Distributed under the Boost Software License, Version 1.0.
// (See accompanying file LICENSE_1_0.txt or copy at
// http://www.boost.org/LICENSE_1_0.txt.)
//

#ifndef BOOST_ENCODING_ENCODING_HPP
#define BOOST_ENCODING_ENCODING_HPP

namespace boost
{
namespace encoding
{

/**
 * Encodes the data in range [\a first,\a last[ using the encoding algorithm \a enc 
 * and stores the result in the range beginning at \a result.
 */
template< typename InputIterator, typename OutputIterator, typename encoding >
OutputIterator encode( InputIterator first, InputIterator last, OutputIterator result, const encoding& enc )
{
    if( last <= first )
        return result;

    while( first != last )
    {
        enc.encode( first, result, last - first );
    }
    return result;
}

/**
 * Decodes the data in range [\a first,\a last[ using the encoding algorithm \a enc 
 * and stores the result in the range beginning at \a result.
 */
template< typename InputIterator, typename OutputIterator, typename encoding >
OutputIterator decode( InputIterator first, InputIterator last, OutputIterator result, const encoding& enc )
{
    if( last <= first )
        return result;

    while( first != last )
    {
        enc.decode( first, result, last - first );
    }
    return result;
}
    
}
}

#endif

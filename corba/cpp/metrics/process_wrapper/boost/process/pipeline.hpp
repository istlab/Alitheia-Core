//
// Boost.Process
//
// Copyright (c) 2006 Julio M. Merino Vidal.
//
// Distributed under the Boost Software License, Version 1.0.
// (See accompanying file LICENSE_1_0.txt or copy at
// http://www.boost.org/LICENSE_1_0.txt.)
//

//!
//! \file boost/process/pipeline.hpp
//!
//! Includes the declaration of the pipeline class.
//!

#if !defined(BOOST_PROCESS_PIPELINE_HPP)
/** \cond */
#define BOOST_PROCESS_PIPELINE_HPP
/** \endcond */

#include <boost/process/basic_pipeline.hpp>
#include <boost/process/command_line.hpp>

namespace boost {
namespace process {

// ------------------------------------------------------------------------

//!
//! \brief Generic instantiation of the basic_pipeline template.
//!
//! Generic instantiation of the basic_pipeline template.  This relies on
//! the command_line implementation of the Command_Line concept provided
//! by the library.
//!
typedef basic_pipeline< command_line > pipeline;

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_PIPELINE_HPP)

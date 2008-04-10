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
//! \file boost/process/children.hpp
//!
//! Includes the declaration of the children class.
//!

#if !defined(BOOST_PROCESS_CHILDREN_HPP)
/** \cond */
#define BOOST_PROCESS_CHILDREN_HPP
/** \endcond */

#include <vector>

#include <boost/assert.hpp>
#include <boost/process/child.hpp>
#include <boost/process/pistream.hpp>
#include <boost/process/postream.hpp>
#include <boost/process/status.hpp>

namespace boost {
namespace process {

// ------------------------------------------------------------------------

//!
//! \brief Representation of a pipelined group of child processes.
//!
//! Represents a group of child process whose standard data streams are
//! connected to form a pipeline.  This higher order structure allows for
//! easy access to the pipeline endpoints and termination synchronization.
//!
class children :
    public std::vector< child >
{
public:
    //!
    //! \brief Returns the pipeline's input stream.
    //!
    //! Returns the pipeline's input stream, which is connected to the
    //! stdin of the first process in the chain.
    //!
    //! \pre The pipeline launcher (pipeline) must have configured
    //!      the first process' stdin to the redirect_stream behavior.
    //!
    postream& get_stdin(void) const;

    //!
    //! \brief Returns the pipeline's output stream.
    //!
    //! Returns the pipeline's output stream, which is connected to the
    //! stdout of the last process in the chain.
    //!
    //! \pre The pipeline launcher (pipeline) must have configured
    //!      the last process' stdout to the redirect_stream behavior.
    //!
    pistream& get_stdout(void) const;

    //!
    //! \brief Returns the pipeline's error stream.
    //!
    //! Returns the pipeline's error stream, which is connected to the
    //! stderr of the last process in the chain.
    //!
    //! \pre The pipeline launcher (pipeline) must have configured
    //!      the last process' stderr to the redirect_stream behavior.
    //!
    pistream& get_stderr(void) const;

    //!
    //! \brief Waits for %children finalization.
    //!
    //! Waits until all the processes in the pipeline have finalized
    //! execution.
    //!
    //! \return The exit status of the first failed process or, if all
    //!         was successful, the exit status of the last process.
    //!
    //! \remark <b>Blocking remarks</b>: This call blocks if any of the
    //! child processes have not finalized execution and waits until they
    //! terminate.
    //!
    status wait(void);
};

// ------------------------------------------------------------------------

postream&
children::get_stdin(void)
    const
{
    BOOST_ASSERT(size() >= 2);

    return (*this)[0].get_stdin();
}

// ------------------------------------------------------------------------

pistream&
children::get_stdout(void)
    const
{
    BOOST_ASSERT(size() >= 2);

    return (*this)[size() - 1].get_stdout();
}

// ------------------------------------------------------------------------

pistream&
children::get_stderr(void)
    const
{
    BOOST_ASSERT(size() >= 2);

    return (*this)[size() - 1].get_stderr();
}

// ------------------------------------------------------------------------

status
children::wait(void)
{
    BOOST_ASSERT(size() >= 2);

    status s = create_status(0);
    status s2 = create_status(0);
    bool update = true;

    for (iterator iter = begin(); iter != end(); iter++) {
        s2 = (*iter).wait();
        if (!s2.exited() || s2.exit_status() != EXIT_SUCCESS) {
            if (update) {
                s = s2;
                update = false;
            }
        }
    }

    return update ? s2 : s;
}

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_CHILDREN_HPP)

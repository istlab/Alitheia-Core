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
//! \file boost/process/child.hpp
//!
//! Includes the declaration of the child class.
//!

#if !defined(BOOST_PROCESS_CHILD_HPP)
/** \cond */
#define BOOST_PROCESS_CHILD_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
extern "C" {
#   include <sys/types.h>
#   include <sys/wait.h>
}
#   include <cerrno>
#   include <boost/process/exceptions.hpp>
#   include <boost/throw_exception.hpp>
#elif defined(BOOST_PROCESS_WIN32_API)
#   include <windows.h>
#else
#   error "Unsupported platform."
#endif

#include <map>

#include <boost/assert.hpp>
#include <boost/process/detail/pipe.hpp>
#include <boost/process/pistream.hpp>
#include <boost/process/postream.hpp>
#include <boost/process/status.hpp>
#include <boost/shared_ptr.hpp>

namespace boost {
namespace process {

namespace detail {
struct factories;
}

// ------------------------------------------------------------------------

//!
//! \brief Generic implementation of the Child concept.
//!
//! The child class implements the Child concept in an operating system
//! agnostic way.
//!
class child
{
public:
#if defined(BOOST_PROCESS_DOXYGEN)
    //!
    //! \brief Opaque name for the native process' handle type.
    //!
    //! Each operating system identifies processes using a specific type.
    //! The \a handle_type type is used to transparently refer to a process
    //! regarless of the operating system in which this class is used.
    //!
    //! If this class is used in a POSIX system, \a NativeSystemHandle is
    //! an integer type while it is a \a HANDLE in a Win32 system.
    //!
    typedef NativeSystemHandle handle_type;
#elif defined(BOOST_PROCESS_WIN32_API)
    typedef HANDLE handle_type;
#elif defined(BOOST_PROCESS_POSIX_API)
    typedef pid_t handle_type;
#endif

    //!
    //! \brief Returns the system handle that identifies the process.
    //!
    //! Returns the system handle that identifies the process.
    //!
    handle_type get_handle(void) const;

    //!
    //! \brief Gets a reference to the child's standard input stream.
    //!
    //! Returns a reference to a postream object that represents the
    //! standard input communication channel with the child process.
    //!
    postream& get_stdin(void) const;

    //!
    //! \brief Gets a reference to the child's standard output stream.
    //!
    //! Returns a reference to a pistream object that represents the
    //! standard output communication channel with the child process.
    //!
    pistream& get_stdout(void) const;

    //!
    //! \brief Gets a reference to the child's standard error stream.
    //!
    //! Returns a reference to a pistream object that represents the
    //! standard error communication channel with the child process.
    //!
    pistream& get_stderr(void) const;

    //!
    //! \brief Blocks and waits for the child process to terminate.
    //!
    //! Returns a status object that represents the child process'
    //! finalization condition.  The child process object ceases to be
    //! valid after this call.
    //!
    //! \remark <b>Blocking remarks</b>: This call blocks if the child
    //! process has not finalized execution and waits until it terminates.
    //!
    status wait(void);

private:
    //!
    //! \brief The handle that identifies the process.
    //!
    handle_type m_handle;

    //!
    //! \brief The standard input stream attached to the child process.
    //!
    //! This postream object holds the communication channel with the
    //! child's process standard input.  It is stored in a pointer because
    //! this field is only valid when the user requested to redirect this
    //! data stream.
    //!
    boost::shared_ptr< postream > m_sstdin;

    //!
    //! \brief The standard output stream attached to the child process.
    //!
    //! This postream object holds the communication channel with the
    //! child's process standard output.  It is stored in a pointer because
    //! this field is only valid when the user requested to redirect this
    //! data stream.
    //!
    boost::shared_ptr< pistream > m_sstdout;

    //!
    //! \brief The standard error stream attached to the child process.
    //!
    //! This postream object holds the communication channel with the
    //! child's process standard error.  It is stored in a pointer because
    //! this field is only valid when the user requested to redirect this
    //! data stream.
    //!
    boost::shared_ptr< pistream > m_sstderr;

protected:
    //!
    //! \brief Constructs a new child object representing a just spawned
    //!        child process.
    //!
    //! Creates a new child object that represents the just spawned process
    //! \a h.
    //!
    //! The \a fhstdin, \a fhstdout and \a fhstderr file handles represent
    //! the parent's handles used to communicate with the corresponding
    //! data streams.  They needn't be valid but their availability must
    //! match the redirections configured by the launcher that spawned this
    //! process.
    //!
    //! This constructor is protected because the library user has no
    //! business in creating representations of live processes himself;
    //! the library takes care of that in all cases.
    //!
    child(handle_type h,
          detail::file_handle fhstdin,
          detail::file_handle fhstdout,
          detail::file_handle fhstderr);
    friend struct detail::factories;
};

// ------------------------------------------------------------------------

inline
child::child(handle_type h,
             detail::file_handle fhstdin,
             detail::file_handle fhstdout,
             detail::file_handle fhstderr) :
    m_handle(h)
{
    if (fhstdin.is_valid())
        m_sstdin.reset(new postream(fhstdin));
    if (fhstdout.is_valid())
        m_sstdout.reset(new pistream(fhstdout));
    if (fhstderr.is_valid())
        m_sstderr.reset(new pistream(fhstderr));
}

// ------------------------------------------------------------------------

inline
child::handle_type
child::get_handle(void)
    const
{
    return m_handle;
}

// ------------------------------------------------------------------------

inline
postream&
child::get_stdin(void)
    const
{
    BOOST_ASSERT(m_sstdin);
    return *m_sstdin;
}

// ------------------------------------------------------------------------

inline
pistream&
child::get_stdout(void)
    const
{
    BOOST_ASSERT(m_sstdout);
    return *m_sstdout;
}

// ------------------------------------------------------------------------

inline
pistream&
child::get_stderr(void)
    const
{
    BOOST_ASSERT(m_sstderr);
    return *m_sstderr;
}

// ------------------------------------------------------------------------

inline
status
child::wait(void)
{
#if defined(BOOST_PROCESS_POSIX_API)
    int s;
    if (::waitpid(m_handle, &s, 0) == -1)
        boost::throw_exception
            (system_error("boost::process::child::wait",
                          "waitpid(2) failed", errno));
    return create_status(s);
#elif defined(BOOST_PROCESS_WIN32_API)
    DWORD code;
    // XXX This loop should go away in favour of a passive wait.
    do {
        ::GetExitCodeProcess(m_handle, &code);
        ::Sleep(500);
    } while (code == STILL_ACTIVE);
    ::WaitForSingleObject(m_handle, 0);
    return create_status(code);
#endif
}

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_CHILD_HPP)

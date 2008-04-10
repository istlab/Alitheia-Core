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
//! \file boost/process/detail/launcher_base.hpp
//!
//! Includes the declaration of the launcher_base class.
//!

#if !defined(BOOST_PROCESS_DETAIL_LAUNCHER_BASE_HPP)
/** \cond */
#define BOOST_PROCESS_DETAIL_LAUNCHER_BASE_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
#elif defined(BOOST_PROCESS_WIN32_API)
#   include <tchar.h>
#   include <windows.h>
#else
#   error "Unsupported platform."
#endif

#include <boost/assert.hpp>
#include <boost/process/detail/environment.hpp>
#include <boost/process/detail/file_handle.hpp>
#include <boost/process/exceptions.hpp>
#include <boost/process/stream_behavior.hpp>
#include <boost/throw_exception.hpp>

namespace boost {
namespace process {
namespace detail {

// ------------------------------------------------------------------------

//!
//! \brief Generic (incomplete) implementation of the Launcher concept.
//!
//! The launcher_base class implements part of the Launcher concept in an
//! operating system agnostic way.  It keeps track of the environment
//! variables, stream's behavior and initial work directory.  The real
//! process spawning method is implemented in classes inheriting this one.
//!
class launcher_base
{
public:
    //!
    //! \brief Constructs a new launcher.
    //!
    //! Constructs a new launcher object ready to spawn a new child
    //! process.  The launcher is configured to close all communcation
    //! channels with the child process unless told otherwise by one of
    //! the set_stdin_behavior(), set_stdout_behavior() or
    //! set_stderr_behavior() methods.
    //!
    //! The initial work directory of the child processes is set to the
    //! current working directory.  See set_work_directory() for more
    //! details.
    //!
    //! The initial environment variables for the child process are
    //! inherited from the parent's table at the moment of creation.
    //!
    launcher_base(void);

    //!
    //! \brief Returns the standard input stream's behavior.
    //!
    //! Returns the standard input stream's behavior.
    //!
    stream_behavior get_stdin_behavior(void) const;

    //!
    //! \brief Sets the standard input stream's behavior.
    //!
    //! Sets the standard input stream's behavior to the value specified
    //! by b.
    //!
    launcher_base& set_stdin_behavior(stream_behavior b);

    //!
    //! \brief Returns the standard output stream's behavior.
    //!
    //! Returns the standard output stream's behavior.
    //!
    stream_behavior get_stdout_behavior(void) const;

    //!
    //! \brief Sets the standard output stream's behavior.
    //!
    //! Sets the standard output stream's behavior to the value specified
    //! by b.
    //!
    launcher_base& set_stdout_behavior(stream_behavior b);

    //!
    //! \brief Returns the standard error stream's behavior.
    //!
    //! Returns the standard error stream's behavior.
    //!
    stream_behavior get_stderr_behavior(void) const;

    //!
    //! \brief Sets the standard error stream's behavior.
    //!
    //! Sets the standard error stream's behavior to the value specified
    //! by b.
    //!
    launcher_base& set_stderr_behavior(stream_behavior b);

    //!
    //! \brief Returns whether stderr is redirected to stdout.
    //!
    //! Returns whether stderr is redirected to stdout.
    //!
    bool get_merge_out_err(void) const;

    //!
    //! \brief Sets the redirection of stderr to stdout.
    //!
    //! Enables or disables the redirection of the standard error stream
    //! to the standard output stream according to the value of b.
    //!
    //! \pre The standard output stream behavior is set to something
    //!      different than close_stream.
    //! \pre The standard error stream behavior is set to close_stream.
    //!
    launcher_base& set_merge_out_err(bool b);

    //!
    //! \brief Clears the %environment variables table.
    //!
    //! Clears the %environment variables table, effectively unsetting them
    //! all.
    //!
    //! It should be noted that under Windows, the empty-named variable is
    //! never removed because it has to point to the initial work
    //! directory.
    //!
    launcher_base& clear_environment(void);

    //!
    //! \brief Sets the %environment variable \a var to \a value.
    //!
    //! Sets the new child's %environment variable \a var to \a value.
    //! The %environment of the current process is not touched.
    //!
    //! If the variable was already defined in the environment, its
    //! contents are replaced with the ones given in \a val.
    //!
    //! Be aware that \a value is allowed to be empty, although this may
    //! result in different behavior depending on the underlying operating
    //! system.  Win32 treats a variable with an empty value the same as
    //! it was undefined.  Contrarywise, POSIX systems consider a variable
    //! with an empty value to be defined.
    //!
    launcher_base& set_environment(const std::string& var,
                              const std::string& value);

    //!
    //! \brief Unsets the %environment variable \a var.
    //!
    //! Unsets the new child's %environment variable \a var.
    //! The %environment of the current process is not touched.
    //!
    launcher_base& unset_environment(const std::string& var);

    //!
    //! \brief Gets the initial work directory for the new child.
    //!
    //! Returns the path to the directory in which the child process will
    //! start operation.
    //!
    const std::string& get_work_directory(void) const;

    //!
    //! \brief Sets the initial work directory for the new child.
    //!
    //! Sets the path to the directory in which the child process will
    //! start operation.
    //!
    launcher_base& set_work_directory(const std::string& wd);

private:
    //!
    //! \brief Child's stdin behavior.
    //!
    stream_behavior m_behavior_in;

    //!
    //! \brief Child's stdout behavior.
    //!
    stream_behavior m_behavior_out;

    //!
    //! \brief Child's stderr behavior.
    //!
    stream_behavior m_behavior_err;

    //!
    //! \brief Whether the child's stderr should be redirected to stdout.
    //!
    bool m_merge_out_err;

    //!
    //! \brief The process' environment.
    //!
    //! Contains the list of environment variables, alongside with their
    //! values, that will be passed to the spawned child process.
    //!
    detail::environment m_environment;

    //!
    //! \brief The process' initial work directory.
    //!
    //! The work directory is the directory in which the process starts
    //! execution.
    //!
    //! Ideally this could be of boost::filesystem::path type but it
    //! is a regular string to avoid depending on Boost.Filesystem.
    //!
    std::string m_work_directory;

protected:
    //!
    //! \brief Returns the child's environment.
    //!
    //! Returns a reference to the child's environment variables.
    //!
    const detail::environment& get_environment(void) const;
};

// ------------------------------------------------------------------------

inline
launcher_base::launcher_base(void) :
    m_behavior_in(close_stream),
    m_behavior_out(close_stream),
    m_behavior_err(close_stream),
    m_merge_out_err(false)
{
#if defined(BOOST_PROCESS_POSIX_API)
    const char* buf = ::getcwd(NULL, 0);
    if (buf == NULL)
        boost::throw_exception
            (system_error
             ("boost::process::launcher_base::launcher_base",
              "getcwd(2) failed", errno));
    m_work_directory = buf;
#elif defined(BOOST_PROCESS_WIN32_API)
    DWORD length = ::GetCurrentDirectory(0, NULL);
    TCHAR* buf = new TCHAR[length * sizeof(TCHAR)];
    if (::GetCurrentDirectory(length, buf) == 0) {
        delete buf;
        boost::throw_exception
            (system_error
             ("boost::process::launcher_base::launcher_base",
              "GetCurrentDirectory failed", ::GetLastError()));
    }
    m_work_directory = buf;
    m_environment.set("", m_work_directory);
    delete buf;
#endif
    BOOST_ASSERT(!m_work_directory.empty());
}

// ------------------------------------------------------------------------

inline
const detail::environment&
launcher_base::get_environment(void)
    const
{
    return m_environment;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::clear_environment(void)
{
    m_environment.clear();
#if defined(BOOST_PROCESS_WIN32_API)
    m_environment.set("", m_work_directory);
#endif
    return *this;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::set_environment(const std::string& var,
                               const std::string& value)
{
    BOOST_ASSERT(!var.empty());
    m_environment.set(var, value);
    return *this;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::unset_environment(const std::string& var)
{
    BOOST_ASSERT(!var.empty());
    m_environment.unset(var);
    return *this;
}

// ------------------------------------------------------------------------

inline
const std::string&
launcher_base::get_work_directory(void)
    const
{
    return m_work_directory;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::set_work_directory(const std::string& wd)
{
    BOOST_ASSERT(wd.length() > 0);
    m_work_directory = wd;
#if defined(BOOST_PROCESS_WIN32_API)
    m_environment.set("", m_work_directory);
#endif
    return *this;
}

// ------------------------------------------------------------------------

inline
stream_behavior
launcher_base::get_stdin_behavior(void)
    const
{
    return m_behavior_in;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::set_stdin_behavior(stream_behavior b)
{
    m_behavior_in = b;
    return *this;
}

// ------------------------------------------------------------------------

inline
stream_behavior
launcher_base::get_stdout_behavior(void)
    const
{
    return m_behavior_out;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::set_stdout_behavior(stream_behavior b)
{
    m_behavior_out = b;
    return *this;
}

// ------------------------------------------------------------------------

inline
stream_behavior
launcher_base::get_stderr_behavior(void)
    const
{
    return m_behavior_err;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::set_stderr_behavior(stream_behavior b)
{
    m_behavior_err = b;
    return *this;
}

// ------------------------------------------------------------------------

inline
bool
launcher_base::get_merge_out_err(void)
    const
{
    return m_merge_out_err;
}

// ------------------------------------------------------------------------

inline
launcher_base&
launcher_base::set_merge_out_err(bool b)
{
    BOOST_ASSERT(!b || m_behavior_err == close_stream);
    BOOST_ASSERT(!b || m_behavior_out != close_stream);
    m_merge_out_err = b;
    return *this;
}

// ------------------------------------------------------------------------

} // namespace detail
} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_DETAIL_LAUNCHER_BASE_HPP)
